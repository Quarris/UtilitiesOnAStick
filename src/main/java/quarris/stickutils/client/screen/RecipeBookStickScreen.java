package quarris.stickutils.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class RecipeBookStickScreen<C extends IInventory, T extends RecipeBookContainer<C>> extends ContainerScreen<T> implements IRecipeShownListener {

    private static final ResourceLocation RECIPE_BUTTON_TEXTURE = new ResourceLocation("textures/gui/recipe_button.png");
    private final RecipeBookGui recipeBookGui = new RecipeBookGui();
    private boolean widthTooNarrow;

    public RecipeBookStickScreen(T container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
    }

    protected void init() {
        super.init();
        this.widthTooNarrow = this.width < 379;
        this.recipeBookGui.init(this.width, this.height, this.minecraft, this.widthTooNarrow, this.container);
        this.guiLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize);
        this.children.add(this.recipeBookGui);
        this.setFocusedDefault(this.recipeBookGui);
        this.addButton(new ImageButton(this.guiLeft + 5, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE, (button) -> {
            this.recipeBookGui.initSearchBar(this.widthTooNarrow);
            this.recipeBookGui.toggleVisibility();
            this.guiLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize);
            ((ImageButton) button).setPosition(this.guiLeft + 5, this.height / 2 - 49);
        }));
    }

    public void tick() {
        super.tick();
        this.recipeBookGui.tick();
    }

    public void render(int mouseX, int mouseY, float pticks) {
        this.renderBackground();
        if (this.recipeBookGui.isVisible() && this.widthTooNarrow) {
            this.drawGuiContainerBackgroundLayer(pticks, mouseX, mouseY);
            this.recipeBookGui.render(mouseX, mouseY, pticks);
        } else {
            this.recipeBookGui.render(mouseX, mouseY, pticks);
            super.render(mouseX, mouseY, pticks);
            this.recipeBookGui.renderGhostRecipe(this.guiLeft, this.guiTop, true, pticks);
        }

        this.renderHoveredToolTip(mouseX, mouseY);
        this.recipeBookGui.renderTooltip(this.guiLeft, this.guiTop, mouseX, mouseY);
        this.func_212932_b(this.recipeBookGui);
    }

    protected abstract ResourceLocation getBackgroundTexture();

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(this.getBackgroundTexture());
        int i = this.guiLeft;
        int j = (this.height - this.ySize) / 2;
        this.blit(i, j, 0, 0, this.xSize, this.ySize);
    }

    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        // TODO might have to change those to be more general
        this.font.drawString(this.title.getFormattedText(), 28.0F, 6.0F, 4210752);
        this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float) (this.ySize - 96 + 2), 4210752);
    }

    protected boolean isPointInRegion(int x, int y, int width, int height, double mouseX, double mouseY) {
        return (!this.widthTooNarrow || !this.recipeBookGui.isVisible()) && super.isPointInRegion(x, y, width, height, mouseX, mouseY);
    }

    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        if (this.recipeBookGui.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
            return true;
        } else {
            return this.widthTooNarrow && this.recipeBookGui.isVisible() || super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
        }
    }

    protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeftIn, int guiTopIn, int mouseButton) {
        boolean flag = mouseX < (double) guiLeftIn || mouseY < (double) guiTopIn || mouseX >= (double) (guiLeftIn + this.xSize) || mouseY >= (double) (guiTopIn + this.ySize);
        return this.recipeBookGui.func_195604_a(mouseX, mouseY, this.guiLeft, this.guiTop, this.xSize, this.ySize, mouseButton) && flag;
    }

    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
        super.handleMouseClick(slotIn, slotId, mouseButton, type);
        this.recipeBookGui.slotClicked(slotIn);
    }

    public void recipesUpdated() {
        this.recipeBookGui.recipesUpdated();
    }

    public void removed() {
        this.recipeBookGui.removed();
        super.removed();
    }

    public RecipeBookGui getRecipeGui() {
        return this.recipeBookGui;
    }
}
