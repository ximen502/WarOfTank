package game


import game.page.InfoWindow
import game.page.KeySetWindow
import java.awt.Color
import java.awt.Font
import java.awt.GridLayout
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import kotlin.system.exitProcess

/**
 * @Class MainWindow
 * @Description 游戏说明窗口
 * @Author xsc
 * @Date 2024/1/6 下午1:07
 * @Version 1.0
 */
class MainWindow : JFrame() {

    init {
        val path = javaClass.getResource("image/bg_main.png")
        println(path)
        var img = ImageIO.read(path)
        val imageIcon = ImageIcon(img)
        var lb = JLabel(imageIcon)
//        lb.setIcon(imageIcon)
        lb.isOpaque = true
        contentPane = lb


        var fl = GridLayout(3, 1)
        fl.hgap = 50
        layout = null

        var lbStart = JButton("开始游戏")
        lbStart.font = Font("宋体", Font.PLAIN, 24)
        lbStart.isOpaque = true
        lbStart.background = Color.ORANGE
        lbStart.setBounds(100, 100, 200, 50)
        lbStart.horizontalAlignment = JLabel.CENTER;
        add(lbStart)

        var lbKeyBoard = JButton("键盘设置")
        lbKeyBoard.font = Font("宋体", Font.PLAIN, 24)
        lbKeyBoard.isOpaque = true
        lbKeyBoard.background = Color.ORANGE
        lbKeyBoard.setBounds(100, 200, 200, 50)
        lbKeyBoard.horizontalAlignment = JLabel.CENTER;
        add(lbKeyBoard)

        var lbAbout = JButton("关于")
        lbAbout.font = Font("宋体", Font.PLAIN, 24)
        lbAbout.isOpaque = true
        lbAbout.background = Color.ORANGE
        lbAbout.setBounds(100, 300, 200, 50)
        lbAbout.horizontalAlignment = JLabel.CENTER;
        add(lbAbout)

        var lbExit = JButton("退出游戏")
        lbExit.font = Font("宋体", Font.PLAIN, 24)
        lbExit.isOpaque = true
        lbExit.background = Color.ORANGE
        lbExit.setBounds(100, 400, 200, 50)
        lbExit.horizontalAlignment = JLabel.CENTER;
        add(lbExit)

        val home = javaClass.getResource("image/hometank.png")
        var imgHome = ImageIO.read(home)
        val iconHome = ImageIcon(imgHome)
        var lbHome = JLabel(iconHome)
        lbHome.setBounds(445, 75, 405, 515)
        add(lbHome)

        title = "坦克大战豪华山寨版"
        //contentPane.background = Color(0x51, 0x3E, 0x57)
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        setSize(875, 654)
        isVisible = true


        lbStart.addActionListener {
            val gameWindow = GameWindow(CP.C * CP.SIZE_M, CP.R * CP.SIZE_M, "坦克大战[ximen502]");
            gameWindow.showLine = false
        }

        lbAbout.addActionListener {
            InfoWindow()
        }

        lbKeyBoard.addActionListener {
            KeySetWindow()
        }

        lbExit.addActionListener {
            exitProcess(1)
        }
    }

}