package frc.robot.reuse.hardware.lowleveldriver;

import java.util.HashMap;
import java.util.Map;

import edu.wpi.first.wpilibj.motorcontrol.Spark;

/**
 * REV Blinkin LED pattern lookup utility.
 * Contains a list of all the patterns so you can look them up via name or index
 */
public final class BlinkinPatterns
{
    public enum PatternType {
        Color_1_Pattern,
        Color_1_and_2_Pattern,
        Color_2_Pattern,
        Fixed_Palette_Pattern,
        Solid_Colors
    }
    public enum PatternName {
        Rainbow_Rainbow_Palette(0),
        Rainbow_Party_Palette(1),
        Rainbow_Ocean_Palette(2),
        Rainbow_Lave_Palette(3),
        Rainbow_Forest_Palette(4),
        Rainbow_with_Glitter(5),
        Confetti(6),
        Shot_Red(7),
        Shot_Blue(8),
        Shot_White(9),
        Sinelon_Rainbow_Palette(10),
        Sinelon_Party_Palette(11),
        Sinelon_Ocean_Palette(12),
        Sinelon_Lava_Palette(13),
        Sinelon_Forest_Palette(14),
        Beats_per_Minute_Rainbow_Palette(15),
        Beats_per_Minute_Party_Palette(16),
        Beats_per_Minute_Ocean_Palette(17),
        Beats_per_Minute_Lava_Palette(18),
        Beats_per_Minute_Forest_Palette(19),
        Fire_Medium(20),
        Fire_Large(21),
        Twinkles_Rainbow_Palette(22),
        Twinkles_Party_Palette(23),
        Twinkles_Ocean_Palette(24),
        Twinkles_Lava_Palette(25),
        Twinkles_Forest_Palette(26),
        Color_Waves_Rainbow_Palette(27),
        Color_Waves_Party_Palette(28),
        Color_Waves_Ocean_Palette(29),
        Color_Waves_Lava_Palette(30),
        Color_Waves_Forest_Palette(31),
        Larson_Scanner_Red(32),
        Larson_Scanner_Gray(33),
        Light_Chase_Red(34),
        Light_Chase_Blue(35),
        Light_Chase_Gray(36),
        Heartbeat_Red(37),
        Heartbeat_Blue(38),
        Heartbeat_White(39),
        Heartbeat_Gray(40),
        Breath_Red(41),
        Breath_Blue(42),
        Breath_Gray(43),
        Strobe_Red(44),
        Strobe_Blue(45),
        Strobe_Gold(46),
        Strobe_White(47),
        Color_1_End_to_End_Blend_to_Black(48),
        Color_1_Larson_Scanner(49),
        Color_1_Light_Chase(50),
        Color_1_Heartbeat_Slow(51),
        Color_1_Heartbeat_Medium(52),
        Color_1_Heartbeat_Fast(53),
        Color_1_Breath_Slow(54),
        Color_1_Breath_Fast(55),
        Color_1_Shot(56),
        Color_1_Strobe(57),
        Color_2_End_to_End_Blend_to_Black(58),
        Color_2_Larson_Scanner(59),
        Color_2_Light_Chase(60),
        Color_2_Heartbeat_Slow(61),
        Color_2_Heartbeat_Medium(62),
        Color_2_Heartbeat_Fast(63),
        Color_2_Breath_Slow(64),
        Color_2_Breath_Fast(65),
        Color_2_Shot(66),
        Color_2_Strobe(67),
        Sparkle_Color_1_on_Color_2(68),
        Sparkle_Color_2_on_Color_1(69),
        Color_Gradient_Color_1_and_2(70),
        Beats_per_Minute_Color_1_and_2(71),
        End_to_End_Blend_Color_1_to_2(72),
        End_to_End_Blend(73),
        Color_1_and_Color_2_no_blending(74),
        Twinkles_Color_1_and_2(75),
        Color_Waves_Color_1_and_2(76),
        Sinelon_Color_1_and_2(77),
        Hot_Pink(78),
        Dark_red(79),
        Red(80),
        Red_Orange(81),
        Orange(82),
        Gold(83),
        Yellow(84),
        Lawn_Green(85),
        Lime(86),
        Dark_Green(87),
        Green(88),
        Blue_Green(89),
        Aqua(90),
        Sky_Blue(91),
        Dark_Blue(92),
        Blue(93),
        Blue_Violet(94),
        Violet(95),
        White(96),
        Gray(97),
        Dark_Gray(98),
        Black(99);

        private final int value;

        private PatternName(int value) { this.value = value; }
        public double get() { return value; }

    }
    public enum Adjustment1 {
        None,
        Dimming,
        Pattern_Density,
        Pattern_Width
    }
    public enum Adjustment2 {
        None,
        Speed
    }

    /**
     * LED Pattern, contains the name and value to write to the {@link Spark},
     * as well as information about what the pattern does
     */
    public final static class Pattern
    {
        /** Value to write to the {@link Spark} */
        public final double value;

        /** Type of Pattern, {@link PatternType} */
        public final PatternType type;

        /** Unique name of the Pattern, {@link PatternName} */
        public final PatternName name;

        /** What Adjustment Dial 1 does, {@link Adjustment1} */
        public final Adjustment1 adj1;

        /** What Adjustment Dial 2 does, {@link Adjustment2} */
        public final Adjustment2 adj2;

        protected Pattern(double value, PatternType type, PatternName name, Adjustment1 adj1, Adjustment2 adj2)
        {
            this.value = value;
            this.type = type;
            this.name = name;
            this.adj1 = adj1;
            this.adj2 = adj2;
        }
    }

    public final static Map<PatternName, Pattern> patterns;

    static {
        patterns = new HashMap<PatternName, Pattern>();

        patterns.put(PatternName.Rainbow_Rainbow_Palette, new Pattern(-0.99, PatternType.Fixed_Palette_Pattern, PatternName.Rainbow_Rainbow_Palette, Adjustment1.Pattern_Density, Adjustment2.Speed));
        patterns.put(PatternName.Rainbow_Party_Palette, new Pattern(-0.97, PatternType.Fixed_Palette_Pattern, PatternName.Rainbow_Party_Palette, Adjustment1.Pattern_Density, Adjustment2.Speed));
        patterns.put(PatternName.Rainbow_Ocean_Palette, new Pattern(-0.95, PatternType.Fixed_Palette_Pattern, PatternName.Rainbow_Ocean_Palette, Adjustment1.Pattern_Density, Adjustment2.Speed));
        patterns.put(PatternName.Rainbow_Lave_Palette, new Pattern(-0.93, PatternType.Fixed_Palette_Pattern, PatternName.Rainbow_Lave_Palette, Adjustment1.Pattern_Density, Adjustment2.Speed));
        patterns.put(PatternName.Rainbow_Forest_Palette, new Pattern(-0.91, PatternType.Fixed_Palette_Pattern, PatternName.Rainbow_Forest_Palette, Adjustment1.Pattern_Density, Adjustment2.Speed));
        patterns.put(PatternName.Rainbow_with_Glitter, new Pattern(-0.89, PatternType.Fixed_Palette_Pattern, PatternName.Rainbow_with_Glitter, Adjustment1.Pattern_Density, Adjustment2.Speed));
        patterns.put(PatternName.Confetti, new Pattern(-0.87, PatternType.Fixed_Palette_Pattern, PatternName.Confetti, Adjustment1.Pattern_Density, Adjustment2.Speed));
        patterns.put(PatternName.Shot_Red, new Pattern(-0.85, PatternType.Fixed_Palette_Pattern, PatternName.Shot_Red, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Shot_Blue, new Pattern(-0.83, PatternType.Fixed_Palette_Pattern, PatternName.Shot_Blue, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Shot_White, new Pattern(-0.81, PatternType.Fixed_Palette_Pattern, PatternName.Shot_White, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Sinelon_Rainbow_Palette, new Pattern(-0.79, PatternType.Fixed_Palette_Pattern, PatternName.Sinelon_Rainbow_Palette, Adjustment1.Pattern_Density, Adjustment2.Speed));
        patterns.put(PatternName.Sinelon_Party_Palette, new Pattern(-0.77, PatternType.Fixed_Palette_Pattern, PatternName.Sinelon_Party_Palette, Adjustment1.Pattern_Density, Adjustment2.Speed));
        patterns.put(PatternName.Sinelon_Ocean_Palette, new Pattern(-0.75, PatternType.Fixed_Palette_Pattern, PatternName.Sinelon_Ocean_Palette, Adjustment1.Pattern_Density, Adjustment2.Speed));
        patterns.put(PatternName.Sinelon_Lava_Palette, new Pattern(-0.73, PatternType.Fixed_Palette_Pattern, PatternName.Sinelon_Lava_Palette, Adjustment1.Pattern_Density, Adjustment2.Speed));
        patterns.put(PatternName.Sinelon_Forest_Palette, new Pattern(-0.71, PatternType.Fixed_Palette_Pattern, PatternName.Sinelon_Forest_Palette, Adjustment1.Pattern_Density, Adjustment2.Speed));
        patterns.put(PatternName.Beats_per_Minute_Rainbow_Palette, new Pattern(-0.69, PatternType.Fixed_Palette_Pattern, PatternName.Beats_per_Minute_Rainbow_Palette, Adjustment1.Pattern_Density, Adjustment2.Speed));
        patterns.put(PatternName.Beats_per_Minute_Party_Palette, new Pattern(-0.67, PatternType.Fixed_Palette_Pattern, PatternName.Beats_per_Minute_Party_Palette, Adjustment1.Pattern_Density, Adjustment2.Speed));
        patterns.put(PatternName.Beats_per_Minute_Ocean_Palette, new Pattern(-0.65, PatternType.Fixed_Palette_Pattern, PatternName.Beats_per_Minute_Ocean_Palette, Adjustment1.Pattern_Density, Adjustment2.Speed));
        patterns.put(PatternName.Beats_per_Minute_Lava_Palette, new Pattern(-0.63, PatternType.Fixed_Palette_Pattern, PatternName.Beats_per_Minute_Lava_Palette, Adjustment1.Pattern_Density, Adjustment2.Speed));
        patterns.put(PatternName.Beats_per_Minute_Forest_Palette, new Pattern(-0.61, PatternType.Fixed_Palette_Pattern, PatternName.Beats_per_Minute_Forest_Palette, Adjustment1.Pattern_Density, Adjustment2.Speed));
        patterns.put(PatternName.Fire_Medium, new Pattern(-0.59, PatternType.Fixed_Palette_Pattern, PatternName.Fire_Medium, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Fire_Large, new Pattern(-0.57, PatternType.Fixed_Palette_Pattern, PatternName.Fire_Large, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Twinkles_Rainbow_Palette, new Pattern(-0.55, PatternType.Fixed_Palette_Pattern, PatternName.Twinkles_Rainbow_Palette, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Twinkles_Party_Palette, new Pattern(-0.53, PatternType.Fixed_Palette_Pattern, PatternName.Twinkles_Party_Palette, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Twinkles_Ocean_Palette, new Pattern(-0.51, PatternType.Fixed_Palette_Pattern, PatternName.Twinkles_Ocean_Palette, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Twinkles_Lava_Palette, new Pattern(-0.49, PatternType.Fixed_Palette_Pattern, PatternName.Twinkles_Lava_Palette, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Twinkles_Forest_Palette, new Pattern(-0.47, PatternType.Fixed_Palette_Pattern, PatternName.Twinkles_Forest_Palette, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Color_Waves_Rainbow_Palette, new Pattern(-0.45, PatternType.Fixed_Palette_Pattern, PatternName.Color_Waves_Rainbow_Palette, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Color_Waves_Party_Palette, new Pattern(-0.43, PatternType.Fixed_Palette_Pattern, PatternName.Color_Waves_Party_Palette, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Color_Waves_Ocean_Palette, new Pattern(-0.41, PatternType.Fixed_Palette_Pattern, PatternName.Color_Waves_Ocean_Palette, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Color_Waves_Lava_Palette, new Pattern(-0.39, PatternType.Fixed_Palette_Pattern, PatternName.Color_Waves_Lava_Palette, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Color_Waves_Forest_Palette, new Pattern(-0.37, PatternType.Fixed_Palette_Pattern, PatternName.Color_Waves_Forest_Palette, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Larson_Scanner_Red, new Pattern(-0.35, PatternType.Fixed_Palette_Pattern, PatternName.Larson_Scanner_Red, Adjustment1.Pattern_Width, Adjustment2.Speed));
        patterns.put(PatternName.Larson_Scanner_Gray, new Pattern(-0.33, PatternType.Fixed_Palette_Pattern, PatternName.Larson_Scanner_Gray, Adjustment1.Pattern_Width, Adjustment2.Speed));
        patterns.put(PatternName.Light_Chase_Red, new Pattern(-0.31, PatternType.Fixed_Palette_Pattern, PatternName.Light_Chase_Red, Adjustment1.Dimming, Adjustment2.Speed));
        patterns.put(PatternName.Light_Chase_Blue, new Pattern(-0.29, PatternType.Fixed_Palette_Pattern, PatternName.Light_Chase_Blue, Adjustment1.Dimming, Adjustment2.Speed));
        patterns.put(PatternName.Light_Chase_Gray, new Pattern(-0.27, PatternType.Fixed_Palette_Pattern, PatternName.Light_Chase_Gray, Adjustment1.Dimming, Adjustment2.Speed));
        patterns.put(PatternName.Heartbeat_Red, new Pattern(-0.25, PatternType.Fixed_Palette_Pattern, PatternName.Heartbeat_Red, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Heartbeat_Blue, new Pattern(-0.23, PatternType.Fixed_Palette_Pattern, PatternName.Heartbeat_Blue, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Heartbeat_White, new Pattern(-0.21, PatternType.Fixed_Palette_Pattern, PatternName.Heartbeat_White, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Heartbeat_Gray, new Pattern(-0.19, PatternType.Fixed_Palette_Pattern, PatternName.Heartbeat_Gray, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Breath_Red, new Pattern(-0.17, PatternType.Fixed_Palette_Pattern, PatternName.Breath_Red, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Breath_Blue, new Pattern(-0.15, PatternType.Fixed_Palette_Pattern, PatternName.Breath_Blue, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Breath_Gray, new Pattern(-0.13, PatternType.Fixed_Palette_Pattern, PatternName.Breath_Gray, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Strobe_Red, new Pattern(-0.11, PatternType.Fixed_Palette_Pattern, PatternName.Strobe_Red, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Strobe_Blue, new Pattern(-0.09, PatternType.Fixed_Palette_Pattern, PatternName.Strobe_Blue, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Strobe_Gold, new Pattern(-0.07, PatternType.Fixed_Palette_Pattern, PatternName.Strobe_Gold, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Strobe_White, new Pattern(-0.05, PatternType.Fixed_Palette_Pattern, PatternName.Strobe_White, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Color_1_End_to_End_Blend_to_Black, new Pattern(-0.03, PatternType.Color_1_Pattern, PatternName.Color_1_End_to_End_Blend_to_Black, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Color_1_Larson_Scanner, new Pattern(-0.01, PatternType.Color_1_Pattern, PatternName.Color_1_Larson_Scanner, Adjustment1.Pattern_Width, Adjustment2.Speed));
        patterns.put(PatternName.Color_1_Light_Chase, new Pattern(0.01, PatternType.Color_1_Pattern, PatternName.Color_1_Light_Chase, Adjustment1.Dimming, Adjustment2.Speed));
        patterns.put(PatternName.Color_1_Heartbeat_Slow, new Pattern(0.03, PatternType.Color_1_Pattern, PatternName.Color_1_Heartbeat_Slow, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Color_1_Heartbeat_Medium, new Pattern(0.05, PatternType.Color_1_Pattern, PatternName.Color_1_Heartbeat_Medium, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Color_1_Heartbeat_Fast, new Pattern(0.07, PatternType.Color_1_Pattern, PatternName.Color_1_Heartbeat_Fast, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Color_1_Breath_Slow, new Pattern(0.09, PatternType.Color_1_Pattern, PatternName.Color_1_Breath_Slow, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Color_1_Breath_Fast, new Pattern(0.11, PatternType.Color_1_Pattern, PatternName.Color_1_Breath_Fast, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Color_1_Shot, new Pattern(0.13, PatternType.Color_1_Pattern, PatternName.Color_1_Shot, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Color_1_Strobe, new Pattern(0.15, PatternType.Color_1_Pattern, PatternName.Color_1_Strobe, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Color_2_End_to_End_Blend_to_Black, new Pattern(0.17, PatternType.Color_2_Pattern, PatternName.Color_2_End_to_End_Blend_to_Black, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Color_2_Larson_Scanner, new Pattern(0.19, PatternType.Color_2_Pattern, PatternName.Color_2_Larson_Scanner, Adjustment1.Pattern_Width, Adjustment2.Speed));
        patterns.put(PatternName.Color_2_Light_Chase, new Pattern(0.21, PatternType.Color_2_Pattern, PatternName.Color_2_Light_Chase, Adjustment1.Dimming, Adjustment2.Speed));
        patterns.put(PatternName.Color_2_Heartbeat_Slow, new Pattern(0.23, PatternType.Color_2_Pattern, PatternName.Color_2_Heartbeat_Slow, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Color_2_Heartbeat_Medium, new Pattern(0.25, PatternType.Color_2_Pattern, PatternName.Color_2_Heartbeat_Medium, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Color_2_Heartbeat_Fast, new Pattern(0.27, PatternType.Color_2_Pattern, PatternName.Color_2_Heartbeat_Fast, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Color_2_Breath_Slow, new Pattern(0.29, PatternType.Color_2_Pattern, PatternName.Color_2_Breath_Slow, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Color_2_Breath_Fast, new Pattern(0.31, PatternType.Color_2_Pattern, PatternName.Color_2_Breath_Fast, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Color_2_Shot, new Pattern(0.33, PatternType.Color_2_Pattern, PatternName.Color_2_Shot, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Color_2_Strobe, new Pattern(0.35, PatternType.Color_2_Pattern, PatternName.Color_2_Strobe, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Sparkle_Color_1_on_Color_2, new Pattern(0.37, PatternType.Color_1_and_2_Pattern, PatternName.Sparkle_Color_1_on_Color_2, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Sparkle_Color_2_on_Color_1, new Pattern(0.39, PatternType.Color_1_and_2_Pattern, PatternName.Sparkle_Color_2_on_Color_1, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Color_Gradient_Color_1_and_2, new Pattern(0.41, PatternType.Color_1_and_2_Pattern, PatternName.Color_Gradient_Color_1_and_2, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Beats_per_Minute_Color_1_and_2, new Pattern(0.43, PatternType.Color_1_and_2_Pattern, PatternName.Beats_per_Minute_Color_1_and_2, Adjustment1.Pattern_Density, Adjustment2.Speed));
        patterns.put(PatternName.End_to_End_Blend_Color_1_to_2, new Pattern(0.45, PatternType.Color_1_and_2_Pattern, PatternName.End_to_End_Blend_Color_1_to_2, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.End_to_End_Blend, new Pattern(0.47, PatternType.Color_1_and_2_Pattern, PatternName.End_to_End_Blend, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Color_1_and_Color_2_no_blending, new Pattern(0.49, PatternType.Color_1_and_2_Pattern, PatternName.Color_1_and_Color_2_no_blending, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Twinkles_Color_1_and_2, new Pattern(0.51, PatternType.Color_1_and_2_Pattern, PatternName.Twinkles_Color_1_and_2, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Color_Waves_Color_1_and_2, new Pattern(0.53, PatternType.Color_1_and_2_Pattern, PatternName.Color_Waves_Color_1_and_2, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Sinelon_Color_1_and_2, new Pattern(0.55, PatternType.Color_1_and_2_Pattern, PatternName.Sinelon_Color_1_and_2, Adjustment1.Pattern_Density, Adjustment2.Speed));
        patterns.put(PatternName.Hot_Pink, new Pattern(0.57, PatternType.Solid_Colors, PatternName.Hot_Pink, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Dark_red, new Pattern(0.59, PatternType.Solid_Colors, PatternName.Dark_red, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Red, new Pattern(0.61, PatternType.Solid_Colors, PatternName.Red, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Red_Orange, new Pattern(0.63, PatternType.Solid_Colors, PatternName.Red_Orange, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Orange, new Pattern(0.65, PatternType.Solid_Colors, PatternName.Orange, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Gold, new Pattern(0.67, PatternType.Solid_Colors, PatternName.Gold, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Yellow, new Pattern(0.69, PatternType.Solid_Colors, PatternName.Yellow, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Lawn_Green, new Pattern(0.71, PatternType.Solid_Colors, PatternName.Lawn_Green, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Lime, new Pattern(0.73, PatternType.Solid_Colors, PatternName.Lime, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Dark_Green, new Pattern(0.75, PatternType.Solid_Colors, PatternName.Dark_Green, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Green, new Pattern(0.77, PatternType.Solid_Colors, PatternName.Green, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Blue_Green, new Pattern(0.79, PatternType.Solid_Colors, PatternName.Blue_Green, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Aqua, new Pattern(0.81, PatternType.Solid_Colors, PatternName.Aqua, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Sky_Blue, new Pattern(0.83, PatternType.Solid_Colors, PatternName.Sky_Blue, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Dark_Blue, new Pattern(0.85, PatternType.Solid_Colors, PatternName.Dark_Blue, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Blue, new Pattern(0.87, PatternType.Solid_Colors, PatternName.Blue, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Blue_Violet, new Pattern(0.89, PatternType.Solid_Colors, PatternName.Blue_Violet, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Violet, new Pattern(0.91, PatternType.Solid_Colors, PatternName.Violet, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.White, new Pattern(0.93, PatternType.Solid_Colors, PatternName.White, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Gray, new Pattern(0.95, PatternType.Solid_Colors, PatternName.Gray, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Dark_Gray, new Pattern(0.97, PatternType.Solid_Colors, PatternName.Dark_Gray, Adjustment1.None, Adjustment2.None));
        patterns.put(PatternName.Black, new Pattern(0.99, PatternType.Solid_Colors, PatternName.Black, Adjustment1.None, Adjustment2.None));

    }

    /**
     * Get the pattern via an index, 0 - 99
     * @param index 0 - 99
     * @return {@link Pattern}
     */
    public static Pattern get(int index)
    {
        return patterns.get(PatternName.values()[index]);
    }

    /**
     * Get the pattern via the name
     * @param name
     * @return {@link Pattern}
     */
    public static Pattern get(PatternName name)
    {
        return patterns.get(name);
    }

    /**
     * Get the pattern value to write to the {@link Spark} via an index, 0 - 99
     * @param index 0 - 99
     * @return {@link Pattern}
     */
    public static double getValue(int index)
    {
        return get(index).value;
    }

    /**
     * Get the pattern value to write to the {@link Spark} via the name
     * @param name
     * @return {@link Pattern}
     */
    public static double getValue(PatternName name)
    {
        return get(name).value;
    }
}
