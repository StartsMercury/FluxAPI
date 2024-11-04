package dev.crmodders.flux.api.ui.components.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import finalforeach.cosmicreach.audio.SoundManager;
import finalforeach.cosmicreach.ui.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static finalforeach.cosmicreach.ui.UIElement.*;
import static finalforeach.cosmicreach.ui.UIElement.uiPanelHoverBoundsTex;

@ApiStatus.Experimental
public class AbstractUIObject implements UIObject {
    public static Color defaultTextColor() {
        final var self = new Color();

        // SAFETY: sets the value to WHITE, without clamping
        self.a = 1.0f;
        self.r = 1.0f;
        self.g = 1.0f;
        self.b = 1.0f;

        return self;
    }

    ///////////////////////////////
    // SIZE AND POSITIONING
    ///////////////////////////////

    /**
     * Stores the anchor position and size.
     */
    private final Rectangle bounds;

    @Override
    public void setX(final float x) {
        this.bounds.x = x;
    }

    @Override
    public void setY(final float y) {
        this.bounds.y = y;
    }

    @Override
    public float getWidth() {
        return this.bounds.width;
    }

    @Override
    public float getHeight() {
        return this.bounds.height;
    }

    private HorizontalAnchor horizontalAnchor;

    public HorizontalAnchor getHorizontalAnchor() {
        return this.horizontalAnchor;
    }

    public void setHorizontalAnchor(final HorizontalAnchor horizontalAnchor) {
        Objects.requireNonNull(horizontalAnchor);
        this.horizontalAnchor = horizontalAnchor;
    }

    public void alignToLeft() {
        this.horizontalAnchor = HorizontalAnchor.LEFT_ALIGNED;
    }

    public void centerHorizontally() {
        this.horizontalAnchor = HorizontalAnchor.CENTERED;
    }

    public void alignToRight() {
        this.horizontalAnchor = HorizontalAnchor.RIGHT_ALIGNED;
    }

    private VerticalAnchor verticalAnchor;

    public VerticalAnchor getVerticalAnchor() {
        return this.verticalAnchor;
    }

    public void setVerticalAnchor(final VerticalAnchor verticalAnchor) {
        Objects.requireNonNull(verticalAnchor);
        this.verticalAnchor = verticalAnchor;
    }

    public void alignToTop() {
        this.verticalAnchor = VerticalAnchor.TOP_ALIGNED;
    }

    public void centerVertically() {
        this.verticalAnchor = VerticalAnchor.CENTERED;
    }

    public void alignToBottom() {
        this.verticalAnchor = VerticalAnchor.BOTTOM_ALIGNED;
    }

    /**
     * The main color, usually used to tint text.
     */
    private final Color color;

    /**
     * Buffer used when dealing with the {@code ScissorStack}.
     */
    private final Rectangle scissors;

    private final Vector2 tmpVec;

    private boolean active;

    private Texture buttonTexture;

    private boolean disabled;

    private boolean hovered;

    private @Nullable String text;

    private boolean visible;

    public AbstractUIObject() {
        System.out.println(Gdx.input.getInputProcessor());
        this.bounds = new Rectangle();
        this.horizontalAnchor = HorizontalAnchor.CENTERED;
        this.color = AbstractUIObject.defaultTextColor();
        this.scissors = new Rectangle();
        this.tmpVec = new Vector2();
        this.verticalAnchor = VerticalAnchor.CENTERED;
    }

    public void onCreate() {
    }

    public void onClick() {
    }

    public void onMouseDown() {
    }

    public void onMouseUp() {
    }

    public boolean isHoveredOver(Viewport viewport, float x, float y) {
        float dx = this.getDisplayX(viewport);
        float dy = this.getDisplayY(viewport);
        return x >= dx && y >= dy && x < dx + this.bounds.width && y < dy + this.bounds.height;
    }

    protected float getDisplayX(final Viewport viewport) {
        final var x = this.bounds.x;

        return switch (this.horizontalAnchor) {
            case LEFT_ALIGNED -> x - viewport.getWorldWidth() / 2.0F;
            case RIGHT_ALIGNED -> x + viewport.getWorldWidth() / 2.0F - this.bounds.width;
            default -> x - this.bounds.width / 2.0F;
        };
    }

    protected float getDisplayY(final Viewport viewport) {
        final var y = this.bounds.y;

        return switch (this.verticalAnchor) {
            case TOP_ALIGNED -> y - viewport.getWorldHeight() / 2.0F;
            case BOTTOM_ALIGNED -> y + viewport.getWorldHeight() / 2.0F - this.bounds.height;
            default -> y - this.bounds.height / 2.0F;
        };
    }

    @Override
    public void drawBackground(
            final Viewport viewport,
            final SpriteBatch batch,
            final float mouseX,
            final float mouseY
    ) {
        if (this.visible) {
            this.buttonTexture = uiPanelTex;
            if (Gdx.input.isButtonJustPressed(0) && Gdx.input.isButtonPressed(0)) {
                this.active = true;
            }

            if (this.active && !Gdx.input.isButtonPressed(0)) {
                this.active = false;
                this.onMouseUp();
            }

            if (this.isHoveredOver(viewport, mouseX, mouseY)) {
                if (!this.hovered) {
                    SoundManager.INSTANCE.playSound(onHoverSound);
                    this.hovered = true;
                }

                if (Gdx.input.isButtonJustPressed(0)) {
                    this.onMouseDown();
                }

                if (Gdx.input.isButtonJustPressed(0)) {
                    this.buttonTexture = uiPanelPressedTex;
                }
            } else {
                this.hovered = false;
                if (Gdx.input.isButtonJustPressed(0) && !Gdx.input.isButtonPressed(0)) {
                    this.active = false;
                }
            }

            this.drawElementBackground(viewport, batch);
            if (Gdx.input.isButtonJustPressed(0) && !Gdx.input.isButtonPressed(0)) {
                this.buttonTexture = uiPanelPressedTex;
                this.onClick();
                SoundManager.INSTANCE.playSound(onClickSound);
            }

        }
    }

    private void drawElementBackground(
            final Viewport viewport,
            final SpriteBatch batch
    ) {
        float x = this.getDisplayX(viewport);
        float y = this.getDisplayY(viewport);
        if (!this.active && (!this.hovered || currentlyHeldElement != null)) {
            batch.draw(uiPanelBoundsTex, x, y, 0.0F, 0.0F, this.bounds.width, this.bounds.height, 1.0F, 1.0F, 0.0F, 0, 0, this.buttonTexture.getWidth(), this.buttonTexture.getHeight(), false, true);
        } else {
            batch.draw(uiPanelHoverBoundsTex, x, y, 0.0F, 0.0F, this.bounds.width, this.bounds.height, 1.0F, 1.0F, 0.0F, 0, 0, this.buttonTexture.getWidth(), this.buttonTexture.getHeight(), false, true);
        }

        batch.draw(this.buttonTexture, x + 1.0F, y + 1.0F, 1.0F, 1.0F, this.bounds.width - 2.0F, this.bounds.height - 2.0F, 1.0F, 1.0F, 0.0F, 0, 0, this.buttonTexture.getWidth(), this.buttonTexture.getHeight(), false, true);
    }

    @Override
    public void drawText(final Viewport viewport, final SpriteBatch batch) {
        if (this.visible && this.text != null && !this.text.isEmpty()) {
            float x = this.getDisplayX(viewport);
            float y = this.getDisplayY(viewport);
            FontRenderer.getTextDimensions(viewport, this.text, this.tmpVec);
            if (this.tmpVec.x > this.bounds.width) {
                FontRenderer.drawTextbox(batch, viewport, this.text, x, y, this.bounds.width);
            } else {
                float maxX = x;
                float maxY = y;

                for(int i = 0; i < this.text.length(); ++i) {
                    char c = this.text.charAt(i);
                    FontTexture f = FontRenderer.getFontTexOfChar(c);
                    if (f == null) {
                        c = '?';
                        f = FontRenderer.getFontTexOfChar(c);
                    }

                    TextureRegion texReg = f.getTexRegForChar(c);
                    x -= f.getCharStartPos(c).x % (float)texReg.getRegionWidth();
                    switch (c) {
                        case '\n':
                            y += (float)texReg.getRegionHeight();
                            x = this.bounds.x;
                            maxX = Math.max(maxX, x);
                            maxY = Math.max(maxY, y);
                            break;
                        case ' ':
                            x += f.getCharSize(c).x / 4.0F;
                            maxX = Math.max(maxX, x);
                            break;
                        default:
                            x += f.getCharSize(c).x + f.getCharStartPos(c).x % (float)texReg.getRegionWidth() + 2.0F;
                            maxX = Math.max(maxX, x);
                            maxY = Math.max(maxY, y + (float)texReg.getRegionHeight());
                    }
                }

                x = this.getDisplayX(viewport);
                y = this.getDisplayY(viewport);
                x += this.bounds.width / 2.0F - (maxX - x) / 2.0F;
                y += this.bounds.height / 2.0F - (maxY - y) / 2.0F;

                final var oldColor = new Color(batch.getColor());
                batch.setColor(this.color);
                FontRenderer.drawText(batch, viewport, this.text, x, y);
                batch.setColor(oldColor);
            }
        }
    }

    public String getText() {
        return this.text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    @Override
    public void updateText() {

    }

    @Override
    public void show() {
        this.visible = true;
    }

    @Override
    public void hide() {
        this.visible = false;
    }

    @Override
    public void deactivate() {

    }

    @Override
    public boolean keyDown(int i) {
        return false;
    }

    @Override
    public boolean keyUp(int i) {
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float v, float v1) {
        return false;
    }

    public void setSize(final float width, final float height) {
        this.bounds.setSize(width, height);
    }

    public boolean isVisible() {
        return this.visible;
    }
}
