package quarris.stickutils.client.container;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.recipebook.*;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import quarris.stickutils.common.container.furnace.AbstractFurnaceStickContainer;
import quarris.stickutils.common.container.furnace.BlastStickContainer;
import quarris.stickutils.common.container.furnace.FurnaceStickContainer;
import quarris.stickutils.common.container.furnace.SmokerStickContainer;

@OnlyIn(Dist.CLIENT)
public class FurnaceStickScreen extends ContainerScreen<AbstractFurnaceStickContainer> implements IRecipeShownListener {
    private static final ResourceLocation field_214089_l = new ResourceLocation("textures/gui/recipe_button.png");
    private static final ResourceLocation FURNACE_GUI_TEXTURES = new ResourceLocation("textures/gui/container/furnace.png");
    public final AbstractRecipeBookGui recipeGui;
    private boolean widthTooNarrowIn;

    public FurnaceStickScreen(AbstractFurnaceStickContainer container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
        this.recipeGui = container instanceof FurnaceStickContainer ? new FurnaceRecipeGui() :
                         container instanceof SmokerStickContainer ? new SmokerRecipeGui() :
                         container instanceof BlastStickContainer ? new BlastFurnaceRecipeGui() : null;
    }

    @Override
    public void init() {
        super.init();
        this.widthTooNarrowIn = this.width < 379;
        this.recipeGui.init(this.width, this.height, this.minecraft, this.widthTooNarrowIn, this.container);
        this.guiLeft = this.recipeGui.updateScreenPosition(this.widthTooNarrowIn, this.width, this.xSize);
        this.addButton((new ImageButton(this.guiLeft + 20, this.height / 2 - 49, 20, 18, 0, 0, 19, field_214089_l, (p_214087_1_) -> {
            this.recipeGui.initSearchBar(this.widthTooNarrowIn);
            this.recipeGui.toggleVisibility();
            this.guiLeft = this.recipeGui.updateScreenPosition(this.widthTooNarrowIn, this.width, this.xSize);
            ((ImageButton) p_214087_1_).setPosition(this.guiLeft + 20, this.height / 2 - 49);
        })));

    }

    @Override
    public void tick() {
        super.tick();
        this.recipeGui.tick();
    }

    @Override
    public void render(int x, int y, float partialTicks) {
        this.renderBackground();
        if (this.recipeGui.isVisible() && this.widthTooNarrowIn) {
            this.drawGuiContainerBackgroundLayer(partialTicks, x, y);
            this.recipeGui.render(x, y, partialTicks);
        } else {
            this.recipeGui.render(x, y, partialTicks);
            super.render(x, y, partialTicks);
            this.recipeGui.renderGhostRecipe(this.guiLeft, this.guiTop, true, partialTicks);
        }

        this.renderHoveredToolTip(x, y);
        this.recipeGui.renderTooltip(this.guiLeft, this.guiTop, x, y);
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String s = this.title.getFormattedText();
        this.font.drawString(s, (float) (this.xSize / 2 - this.font.getStringWidth(s) / 2), 6.0F, 4210752);
        GlStateManager.pushMatrix();
        GlStateManager.scalef(0.5f, 0.5f, 0.5f);
        this.font.drawString("on a stick", 122.0F * 2, 6.0F * 2, 4210752);
        GlStateManager.popMatrix();
        this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float) (this.ySize - 96 + 2), 4210752);
    }

    /**
     * Draws the background layer of this container (behind the items).
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(FURNACE_GUI_TEXTURES);
        int i = this.guiLeft;
        int j = this.guiTop;
        this.blit(i, j, 0, 0, this.xSize, this.ySize);
        if (this.container.isActive()) {
            int k = this.container.getBurnLeftScaled();
            this.blit(i + 56, j + 36 + 12 - k, 176, 12 - k, 14, k + 1);
        }

        int l = this.container.getCookProgressionScaled();
        this.blit(i + 79, j + 34, 176, 14, l + 1, 16);
    }

    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        if (this.recipeGui.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
            return true;
        } else {
            return this.widthTooNarrowIn && this.recipeGui.isVisible() ? true : super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
        }
    }

    /**
     * Called when the mouse is clicked over a slot or outside the gui.
     */
    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
        super.handleMouseClick(slotIn, slotId, mouseButton, type);
        this.recipeGui.slotClicked(slotIn);
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        return this.recipeGui.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) ? false : super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeftIn, int guiTopIn, int mouseButton) {
        boolean flag = mouseX < (double) guiLeftIn || mouseY < (double) guiTopIn || mouseX >= (double) (guiLeftIn + this.xSize) || mouseY >= (double) (guiTopIn + this.ySize);
        return this.recipeGui.func_195604_a(mouseX, mouseY, this.guiLeft, this.guiTop, this.xSize, this.ySize, mouseButton) && flag;
    }

    @Override
    public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
        return this.recipeGui.charTyped(p_charTyped_1_, p_charTyped_2_) ? true : super.charTyped(p_charTyped_1_, p_charTyped_2_);
    }

    @Override
    public void recipesUpdated() {
        this.recipeGui.recipesUpdated();
    }

    @Override
    public RecipeBookGui getRecipeGui() {
        return this.recipeGui;
    }

    @Override
    public void removed() {
        this.recipeGui.removed();
        super.removed();
    }
}
