package game.page

import java.awt.*
import javax.imageio.ImageIO
import javax.swing.*


/**
 * @Class InfoWindow
 * @Description 信息介绍页面
 * @Author xsc
 * @Date 2024/1/12 上午10:01
 * @Version 1.0
 */
class InfoWindow : JFrame() {
    init {
        val content = "荀子：故不积跬步，无以至千里；不积小流，无以成江海。"
        val lb = JLabel(content)
        lb.font = Font("宋体", Font.PLAIN, 24)
//        lb.foreground = Color(0xf4, 0x60, 0x6c)
        lb.foreground = Color(0x19, 0xca, 0xad)

        //修改资源的访问路径，避免打包为可执行jar后读取资源失败
        val path = javaClass.getResource("/game/image/xunzi.jpg")
        var img = ImageIO.read(path)
        val imageIcon = ImageIcon(img)
        val lbHead = JLabel(imageIcon)
        val jpWord = JPanel(FlowLayout())
        jpWord.background = Color(0, 0, 0, 0)
//        jpWord.add(lbHead)
        jpWord.add(lb)


        //layout = FlowLayout()
        title = "关于"
        defaultCloseOperation = JFrame.HIDE_ON_CLOSE
        setSize(875, 654)
        isVisible = true
        contentPane.background = Color.WHITE//
        setLocationRelativeTo(null)
        add(jpWord, BorderLayout.SOUTH)

        val author = "一个程序员，偶尔使用古老技术实现一些需求，做点个人感兴趣的事情"
        val lbAuthor = JLabel(author)
        lbAuthor.foreground = Color(0x8C, 0xC7, 0xB5)
        lbAuthor.font = Font("宋体", Font.PLAIN, 20)
        val jpAuthor = JPanel()
        val constraints = GridBagConstraints()
        constraints.gridx = 0
        constraints.gridy = 0
        constraints.weightx = 1.0
        constraints.weighty = 1.0
        jpAuthor.add(lbAuthor, constraints)
        jpAuthor.background = Color(0, 0, 0, 0)
        //add(jpAuthor, BorderLayout.CENTER)

        val thanks = "感谢B站UP主 【蜘蛛骑士又怎么了】【C语言实战大全】，参考过他们的坦克大战视频教程"
        val github = "https://github.com/ximen502/WarOfTank"
        val taGH = JTextArea(github)
        taGH.isEditable = false
        val jpGH = JPanel(FlowLayout())
        jpGH.background = Color(0, 0, 0, 0)
        taGH.append("\n$thanks")
        jpGH.add(taGH)

//        val lbThanks = JLabel(thanks)
        val jpThanks = JPanel(FlowLayout())
        jpThanks.background = Color(0, 0, 0, 0)
//        jpThanks.add(lbThanks)
        jpThanks.add(lbHead)

        val centerPanel = JPanel(BorderLayout())
        centerPanel.background = Color(0, 0, 0, 0)
        centerPanel.add(jpAuthor, BorderLayout.NORTH)
        centerPanel.add(jpGH, BorderLayout.CENTER)
        centerPanel.add(jpThanks, BorderLayout.SOUTH)
        add(centerPanel, BorderLayout.CENTER)

        val desc = "这个山寨版小游戏，耗时约五个月，一路走来，筚路蓝缕，经历了一些曲折和看似很难解决的问题，" +
                "在遇到困难的时候，就先放下，偶尔想起来，就想想解决方法，或从互联网搜索一些视频讲解，看一下大佬的解决思路，" +
                "随着探索和研究的进步，问题最终都一一解决了，大的问题拆分成小的问题，每天进步一点点，每天解决一点点，积累起来" +
                "就是巨大的进步，正所谓：不积跬步，无以至千里；不积小流，无以成江海。"
        val taDesc = JTextArea(desc)
        taDesc.rows = 6
        //lbDesc.wrapStyleWord = true
        taDesc.lineWrap = true
        taDesc.isEditable = false
        taDesc.foreground = Color.GRAY
        taDesc.font = Font("宋体", Font.PLAIN, 20)
        val jsp = JScrollPane(taDesc)
        add(jsp, BorderLayout.NORTH)
    }
}