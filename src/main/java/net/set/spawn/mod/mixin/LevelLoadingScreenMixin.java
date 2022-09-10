package net.set.spawn.mod.mixin;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.LevelLoadingScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.set.spawn.mod.Conditionals;
import net.set.spawn.mod.config.SetSpawnProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelLoadingScreen.class)
public abstract class LevelLoadingScreenMixin extends Screen {

    protected LevelLoadingScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void displaySetSpawnOnF3(int mouseX, int mouseY, float delta, CallbackInfo ci){
        SetSpawnProperties.init();
        if (Conditionals.isModActive) {
            TextRenderer textRenderer = this.font;
            int horizontalCenter = this.width / 2;
            int verticalCenter = this.height / 2;
            int verticalPlacement = verticalCenter + 85;
            int white = 16777215;
            this.drawCenteredString(textRenderer, SetSpawnProperties.coordinates, horizontalCenter, verticalPlacement, white);
        }
    }
}
