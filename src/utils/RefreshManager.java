package utils;

import ui.ChartsPanel;

public class RefreshManager {

    private static ChartsPanel chartsPanel;

    public static void registerChartsPanel(ChartsPanel panel) {
        chartsPanel = panel;
    }

    public static void refreshCharts() {
        if (chartsPanel != null) {
            chartsPanel.refresh();
        }
    }
}