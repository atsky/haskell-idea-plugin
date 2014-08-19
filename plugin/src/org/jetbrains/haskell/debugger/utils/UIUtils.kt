package org.jetbrains.haskell.debugger.utils

import com.intellij.notification.Notifications
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import javax.swing.JPanel
import javax.swing.JComponent
import org.jetbrains.haskell.util.gridBagConstraints
import java.awt.Insets
import javax.swing.JLabel
import org.jetbrains.haskell.util.setConstraints
import java.awt.GridBagConstraints
import javax.swing.Box

/**
 * @author Habibullin Marat
 */
public class UIUtils {
    class object {
        public fun addLabeledControl(panel: JPanel, row: Int, label: String, component: JComponent) {
            val base = gridBagConstraints { insets = Insets(2, 0, 2, 3) }
            panel.add(JLabel(label), base.setConstraints {
                anchor = GridBagConstraints.LINE_START
                gridx = 0;
                gridy = row;
            })
            panel.add(component, base.setConstraints {
                gridx = 1;
                gridy = row;
                fill = GridBagConstraints.HORIZONTAL
                weightx = 1.0
            })
            panel.add(Box.createHorizontalStrut(1), base.setConstraints {
                gridx = 2;
                gridy = row;
                weightx = 0.1
            })
        }

        public fun notifyCommandInProgress() {
            val msg = "Some command is in progress, it must finish first"
            Notifications.Bus.notify(Notification("", "Can't perform action", msg, NotificationType.WARNING))
        }
    }
}