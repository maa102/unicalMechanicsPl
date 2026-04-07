package ma102.vervain.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ColorUtils {

    public static final Map<String, GradientColor> GRADIENTS = new LinkedHashMap<>();

    static {
        GRADIENTS.put("§c§lОГНЕННЫЙ", new GradientColor("§c§lОГНЕННЫЙ", Arrays.asList(0xFF4444, 0xFF8844, 0xFFCC44, 0xFF8844, 0xFF4444), 20));
        GRADIENTS.put("§d§lНЕОНОВЫЙ", new GradientColor("§d§lНЕОНОВЫЙ", Arrays.asList(0xFF44FF, 0xFF88FF, 0xFF44FF, 0xFF88FF), 15));
        GRADIENTS.put("§b§lАКВАМАРИН", new GradientColor("§b§lАКВАМАРИН", Arrays.asList(0x44FFFF, 0x88FFFF, 0x44CCFF, 0x88FFFF), 15));
        GRADIENTS.put("§a§lИЗУМРУД", new GradientColor("§a§lИЗУМРУД", Arrays.asList(0x44FF88, 0x88FFAA, 0x44DD66, 0x88FFAA), 15));
        GRADIENTS.put("§6§lЗОЛОТОЙ", new GradientColor("§6§lЗОЛОТОЙ", Arrays.asList(0xFFAA33, 0xFFDD66, 0xFFAA33, 0xFFDD66), 15));
        GRADIENTS.put("§5§lФИОЛЕТОВЫЙ", new GradientColor("§5§lФИОЛЕТОВЫЙ", Arrays.asList(0xAA44FF, 0xCC66FF, 0xAA44FF, 0xCC66FF), 15));
        GRADIENTS.put("§3§lОКЕАН", new GradientColor("§3§lОКЕАН", Arrays.asList(0x2266DD, 0x4488FF, 0x2266DD, 0x4488FF), 15));
        GRADIENTS.put("§c§l§k§lРАДУГА", new GradientColor("§c§lРАДУГА", Arrays.asList(0xFF4444, 0xFFAA44, 0xFFFF44, 0x44FF44, 0x44FFFF, 0x4444FF, 0xFF44FF), 10));
        GRADIENTS.put("§f§lЛЁД", new GradientColor("§f§lЛЁД", Arrays.asList(0xFFFFFF, 0xCCFFFF, 0x99FFFF, 0xCCFFFF, 0xFFFFFF), 20));
        GRADIENTS.put("§8§lТЕНЬ", new GradientColor("§8§lТЕНЬ", Arrays.asList(0x666666, 0x888888, 0xAAAAAA, 0x888888, 0x666666), 20));
    }

    public static class GradientColor {
        private final String name;
        private final List<Integer> colors;
        private final int speed;

        public GradientColor(String name, List<Integer> colors, int speed) {
            this.name = name;
            this.colors = colors;
            this.speed = speed;
        }

        public String getName() {
            return name;
        }

        public List<Integer> getColors() {
            return colors;
        }

        public int getSpeed() {
            return speed;
        }

        public Component getAnimatedComponent(String text, int tick) {
            StringBuilder result = new StringBuilder();
            int colorIndex = (tick / speed) % colors.size();
            for (int i = 0; i < text.length(); i++) {
                int currentColor = colors.get((colorIndex + i) % colors.size());
                result.append("§x");
                String hex = String.format("%06X", currentColor);
                for (char c : hex.toCharArray()) {
                    result.append("§").append(c);
                }
                result.append(text.charAt(i));
            }
            return Component.text(result.toString()).decoration(TextDecoration.ITALIC, false);
        }
    }
}
