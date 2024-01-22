package game.page

import java.awt.*
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel


/**
 * @Class KeySetWindow
 * @Description 键盘设置页面
 * @Author xsc
 * @Date 2024/1/15 下午10:24
 * @Version 1.0
 */
class KeySetWindow : JFrame() {

    init {

//        layout = GridLayout(2, 1)
        title = "键盘设置"
        defaultCloseOperation = JFrame.HIDE_ON_CLOSE
        setSize(875, 654)
        isVisible = true
        contentPane.background = Color.WHITE//


        val content = "键盘设置目前暂时不支持，目前的按键功能如下所示"
        val lb = JLabel(content)
        lb.font = Font("宋体", Font.PLAIN, 24)
//        lb.foreground = Color(0xf4, 0x60, 0x6c)
        lb.foreground = Color(0xD1,0xBA,0x74)
        var jp = JPanel(FlowLayout())
        jp.add(lb)
        add(jp, BorderLayout.NORTH)

        val path = javaClass.getResource("../image/bg_key.png")
        println(path)
        var img = ImageIO.read(path)
        val imageIcon = ImageIcon(img)
        val lbIcon = JLabel()
        lbIcon.icon = imageIcon

        val panel = JPanel(GridBagLayout())
        val constraints = GridBagConstraints()
        constraints.gridx = 0
        constraints.gridy = 0
        constraints.weightx = 1.0
        constraints.weighty = 1.0
        panel.add(lbIcon, constraints)
        add(panel, BorderLayout.CENTER)

//        var jpIcon = JPanel(FlowLayout())
//        jpIcon.add(lbIcon)
//        add(jpIcon)
    }
}