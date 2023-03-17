package dev.uten2c.raincoat.option;

import dev.uten2c.raincoat.Networking;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.MouseOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionListWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RaincoatOptionsScreen extends GameOptionsScreen {
    private static final Text TOGGLE_KEY_TEXT = Text.translatable("raincoat.options.key.toggle");
    private static final Text HOLD_KEY_TEXT = Text.translatable("raincoat.options.key.hold");

    private final Screen parent;
    private final GameOptions gameOptions;
    private OptionListWidget buttonList;

    private final SimpleOption<@NotNull
            Double> ads = new SimpleOption<>(
            "raincoat.options.ads_relative_sensitivity",
            SimpleOption.emptyTooltip(),
            (text, value) -> Text.translatable("options.percent_value", text, (int) (value * 100.0)),
            SimpleOption.DoubleSliderCallbacks.INSTANCE, 1.0, value -> {
    });
    private final SimpleOption<@NotNull Double> scope2x = new SimpleOption<>(
            "raincoat.options.2x_zoom_relative_sensitivity",
            SimpleOption.emptyTooltip(),
            (text, value) -> Text.translatable("options.percent_value", text, (int) (value * 100.0)),
            SimpleOption.DoubleSliderCallbacks.INSTANCE, 0.5, value -> {
    });
    private final SimpleOption<@NotNull Double> scope4x = new SimpleOption<>(
            "raincoat.options.4x_zoom_relative_sensitivity",
            SimpleOption.emptyTooltip(),
            (text, value) -> Text.translatable("options.percent_value", text, (int) (value * 100.0)),
            SimpleOption.DoubleSliderCallbacks.INSTANCE, 0.25, value -> {
    });
    private final SimpleOption<Boolean> adsMode = new SimpleOption<>(
            "raincoat.options.ads",
            SimpleOption.emptyTooltip(),
            (optionText, value) -> value ? HOLD_KEY_TEXT : TOGGLE_KEY_TEXT, SimpleOption.BOOLEAN, false, value -> {
    });
    private final SimpleOption<Boolean> invertAttackKey = SimpleOption.ofBoolean(
            "raincoat.options.invert_attack_key",
            false,
            value -> {
            }
    );
    private final SimpleOption<Boolean> hideCrosshairWhenAds = SimpleOption.ofBoolean(
            "raincoat.options.hide_crosshair_when_ads",
            false,
            value -> {
            }
    );

    public RaincoatOptionsScreen(@Nullable Screen parent, GameOptions gameOptions) {
        super(parent, gameOptions, Text.translatable("raincoat.options.title"));
        this.parent = parent;
        this.gameOptions = gameOptions;
    }

    @Override
    protected void init() {
        ads.setValue(Options.getAdsRelativeSensibility());
        scope2x.setValue(Options.getScope2xRelativeSensibility());
        scope4x.setValue(Options.getScope4xRelativeSensibility());
        adsMode.setValue(Options.isAdsHold());
        invertAttackKey.setValue(Options.isInvertAttackKey());
        hideCrosshairWhenAds.setValue(Options.isHideCrosshairWhenAds());

        this.buttonList = new OptionListWidget(this.client, this.width, this.height, 32, this.height - 32, 25);
        buttonList.addOptionEntry(adsMode, invertAttackKey);
        if (InputUtil.isRawMouseMotionSupported()) {
            buttonList.addOptionEntry(gameOptions.getMouseSensitivity(), gameOptions.getRawMouseInput());
        } else {
            buttonList.addSingleOptionEntry(gameOptions.getMouseSensitivity());
        }
        buttonList.addSingleOptionEntry(ads);
        buttonList.addSingleOptionEntry(scope2x);
        buttonList.addSingleOptionEntry(scope4x);
        buttonList.addOptionEntry(hideCrosshairWhenAds, null);
        this.addSelectableChild(this.buttonList);
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> {
            assert this.client != null;
            gameOptions.write();
            this.client.setScreen(this.parent);
        }).dimensions(this.width / 2 - 100, this.height - 27, 200, 20).build());
    }

    @Override
    public void removed() {
        Options.setAdsRelativeSensibility(roundSensitivity(ads.getValue()));
        Options.setScope2xRelativeSensibility(roundSensitivity(scope2x.getValue()));
        Options.setScope4xRelativeSensibility(roundSensitivity(scope4x.getValue()));
        Options.setAdsHold(adsMode.getValue());
        Options.setInvertAttackKey(invertAttackKey.getValue());
        Options.setHideCrosshairWhenAds(hideCrosshairWhenAds.getValue());
        Options.save();
        Networking.sendSettingsUpdate();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.buttonList.render(matrices, mouseX, mouseY, delta);
        MouseOptionsScreen.drawCenteredTextWithShadow(matrices, this.textRenderer, this.title, this.width / 2, 5, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
    }

    private static double roundSensitivity(double value) {
        return Math.floor(value * 100.0) / 100.0;
    }
}
