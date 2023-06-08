package dev.uten2c.raincoat.option

import dev.uten2c.raincoat.network.Networking.sendSettingsUpdate
import dev.uten2c.raincoat.option.OptionManager.options
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.option.GameOptionsScreen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.OptionListWidget
import net.minecraft.client.option.GameOptions
import net.minecraft.client.option.SimpleOption
import net.minecraft.client.util.InputUtil
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text
import kotlin.math.floor

class RaincoatOptionsScreen(parent: Screen?, gameOptions: GameOptions) :
    GameOptionsScreen(parent, gameOptions, screenTitle) {
    private val adsRelativeSensibility = createSensibilityOption(
        "raincoat.options.ads_relative_sensitivity",
        options.adsRelativeSensibility,
    )
    private val scope2xRelativeSensibility = createSensibilityOption(
        "raincoat.options.2x_zoom_relative_sensitivity",
        options.scope2xRelativeSensibility,
    )
    private val scope4xRelativeSensibility = createSensibilityOption(
        "raincoat.options.4x_zoom_relative_sensitivity",
        options.scope4xRelativeSensibility,
    )
    private val adsHold = SimpleOption(
        "raincoat.options.ads",
        SimpleOption.emptyTooltip(),
        { _, value -> if (value) HOLD_KEY_TEXT else TOGGLE_KEY_TEXT },
        SimpleOption.BOOLEAN,
        options.isAdsHold,
        {},
    )
    private val invertAttackKey = SimpleOption.ofBoolean(
        "raincoat.options.invert_attack_key",
        options.isInvertAttackKey,
    )
    private val hideCrosshairWhenAds = SimpleOption.ofBoolean(
        "raincoat.options.hide_crosshair_when_ads",
        options.isHideCrosshairWhenAds,
    )
    private lateinit var buttonList: OptionListWidget

    override fun init() {
        buttonList = OptionListWidget(client, width, height, 32, height - 32, 25)
        buttonList.addOptionEntry(adsHold, invertAttackKey)
        if (InputUtil.isRawMouseMotionSupported()) {
            buttonList.addOptionEntry(gameOptions.mouseSensitivity, gameOptions.rawMouseInput)
        } else {
            buttonList.addSingleOptionEntry(gameOptions.mouseSensitivity)
        }
        buttonList.addSingleOptionEntry(adsRelativeSensibility)
        buttonList.addSingleOptionEntry(scope2xRelativeSensibility)
        buttonList.addSingleOptionEntry(scope4xRelativeSensibility)
        buttonList.addOptionEntry(hideCrosshairWhenAds, null)
        addSelectableChild(this.buttonList)
        addDrawableChild(
            ButtonWidget.builder(ScreenTexts.DONE) {
                gameOptions.write()
                client!!.setScreen(this.parent)
            }.dimensions(width / 2 - 100, height - 27, 200, 20).build(),
        )
    }

    override fun removed() {
        val newOptions = RaincoatOptions(
            adsRelativeSensibility.value,
            scope2xRelativeSensibility.value,
            scope4xRelativeSensibility.value,
            adsHold.value,
            invertAttackKey.value,
            hideCrosshairWhenAds.value,
        )
        OptionManager.save(newOptions)
        sendSettingsUpdate()
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(context)
        buttonList.render(context, mouseX, mouseY, delta)
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 5, 0xFFFFFF)
        super.render(context, mouseX, mouseY, delta)
    }

    companion object {
        private val screenTitle = Text.translatable("raincoat.options.title")
        private val TOGGLE_KEY_TEXT = Text.translatable("raincoat.options.key.toggle")
        private val HOLD_KEY_TEXT = Text.translatable("raincoat.options.key.hold")
        private val sliderCallbacks = SimpleOption.DoubleSliderCallbacks.INSTANCE.withModifier(
            { if (it in 0.0..1.0) it else 0.0 },
            { roundSensitivity(it) },
        )

        private fun roundSensitivity(value: Double): Double {
            return floor(value * 100.0) / 100.0
        }

        private fun createSensibilityOption(
            key: String,
            defaultValue: Double,
        ): SimpleOption<Double> {
            return SimpleOption(
                key,
                SimpleOption.emptyTooltip(),
                { text, value -> Text.translatable("options.percent_value", text, (value * 100.0).toInt()) },
                sliderCallbacks,
                defaultValue,
            ) {}
        }
    }
}
