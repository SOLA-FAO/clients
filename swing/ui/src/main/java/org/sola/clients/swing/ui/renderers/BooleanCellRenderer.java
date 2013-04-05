package org.sola.clients.swing.ui.renderers;

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Displays check image for the true value and minus for the false value.
 */
public class BooleanCellRenderer extends DefaultTableCellRenderer {
    private ImageIcon imageTrue;
    private ImageIcon imageFalse;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value,
                isSelected, hasFocus, row, column);

        if (value != null) {
            label.setHorizontalTextPosition(JLabel.CENTER);
            label.setVerticalTextPosition(JLabel.CENTER);
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setVerticalAlignment(JLabel.CENTER);

            try {
                Boolean realValue = (Boolean) value;
                // Load images
                if (imageTrue == null) {
                    imageTrue = new ImageIcon(BooleanCellRenderer.class.getResource(
                            "/images/common/confirm.png"));
                }
                if (imageFalse == null) {
                    imageFalse = new ImageIcon(BooleanCellRenderer.class.getResource(
                            "/images/common/minus.png"));
                }
                
                if(realValue){
                    label.setIcon(imageTrue);
                }else{
                    label.setIcon(imageFalse);
                }
                
            } catch (Exception ex) {
            }
        }

        label.setText(null);
        return this;
    }
}
