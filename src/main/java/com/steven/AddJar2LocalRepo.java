package com.steven;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.util.Arrays;

public class AddJar2LocalRepo {
    // 获取一个静态全局的JFrame对象
    public static JFrame jf;
    public static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    public static double scale = 0.5;
    public static File selectedFile;
    public static File bat_file;
    public static String copy_jar_file;
    public static String sys_encode="GBK";


    // 获取系统的分辨率

    static {
        // 加载UI
        try {
//            去除顶部设置按钮
            UIManager.put("RootPane.setupButtonVisible", false);
            org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
            // jframe对象需要放在加载UI主题包之后,初始化,否则可能会造成部分效果设置不生效的问题
            jf = new JFrame();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(jf, "UI主题包设置异常!!!");
        }
    }

    public static void main(String[] args) throws IOException {
        Process chcp_process = Runtime.getRuntime().exec("cmd /c chcp");
        InputStream chcp_in = chcp_process.getInputStream();
        BufferedReader chcp_reader = new BufferedReader(new InputStreamReader(chcp_in));
        String chcp = chcp_reader.readLine().split(":")[1].trim();
        switch (chcp){
            case "936" : sys_encode = "GBK";break;
            case "65001" : sys_encode = "UTF-8";break;
        }
        /*
        jar文件发布到本地仓库
         mvn install:install-file
         -DgroupId= com.offcn
         -DartifactId= swing-sets3
         -Dversion=1.0.0
         -Dpackaging=jar
         -Dfile=D:\\swing-sets3.jar
         */
        jf.setTitle("添加jar包到本地仓库");
        jf.setSize(dimension2Int(screenSize.getWidth() * scale), dimension2Int(screenSize.getHeight() * scale));
        jf.setLocationRelativeTo(null);
        jf.setResizable(false);

        ImageIcon bg = new ImageIcon(AddJar2LocalRepo.class.getResource("/appIcon/bg.jpg"));
        JLabel bg_label = new JLabel(bg);
        bg_label.setBounds(0, 0, bg.getIconWidth(), bg.getIconHeight());
        jf.getLayeredPane().add(bg_label, new Integer(Integer.MIN_VALUE));
        // 设置系统软件图标
        ImageIcon app_icon = new ImageIcon(AddJar2LocalRepo.class.getResource("/appIcon/addjar2LocalRepo.jpg"));
        jf.setIconImage(app_icon.getImage());
        GridLayout layout = new GridLayout(6, 1, 10, 0);

        jf.setLayout(layout);
//        Container pane = jf.getContentPane();


//        pane.setLayout();
        JPanel jPanel_title = new JPanel(new FlowLayout());
        JLabel title = new JLabel("发布JAR包->本地仓库");
        title.setFont(new Font("楷体", Font.BOLD, 40));
        title.setForeground(new Color(245, 70, 23));
        jPanel_title.add(title);

        JPanel jPanel_location = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JLabel space = new JLabel("         ");
        space.setFont(new Font("楷体", Font.BOLD, 25));
        space.setForeground(new Color(14, 145, 253));

        JButton JAR_Location = new JButton("选择Jar包位置");
        JAR_Location.setFont(new Font("楷体", Font.BOLD, 25));
        JAR_Location.setForeground(new Color(14, 214, 169));

        JTextField jar_file_loc = new JTextField("");
        jar_file_loc.setColumns(dimension2Int(jf.getWidth()) / 30);
        jar_file_loc.setForeground(new Color(14, 145, 253));
        jar_file_loc.setFont(new Font("楷体", Font.BOLD, 18));
        jPanel_location.add(space);
        jPanel_location.add(JAR_Location);
        jPanel_location.add(jar_file_loc);

        JPanel jPanel_groupId = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JLabel groupId = new JLabel("          GroupId   ", JLabel.LEFT);
        groupId.setFont(new Font("Consolas", Font.BOLD, 25));
        JTextField groupId_info = new JTextField("org.example");
        groupId_info.setColumns(dimension2Int(jf.getWidth()) / 30);
        groupId_info.setForeground(new Color(14, 145, 253));
        groupId_info.setFont(new Font("楷体", Font.BOLD, 20));
        groupId.setForeground(new Color(236,224,13));
        jPanel_groupId.add(groupId);
        jPanel_groupId.add(groupId_info);

        JPanel jPanel_artifactId = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JLabel artifactId = new JLabel("          ArtifactId", JLabel.LEFT);
        artifactId.setForeground(new Color(236,224,13));
        artifactId.setFont(new Font("Consolas", Font.BOLD, 25));
        JTextField artifactId_info = new JTextField("artifactId");
        artifactId_info.setColumns(dimension2Int(jf.getWidth()) / 30);
        artifactId_info.setForeground(new Color(14, 145, 253));
        artifactId_info.setFont(new Font("楷体", Font.BOLD, 20));
        jPanel_artifactId.add(artifactId);
        jPanel_artifactId.add(artifactId_info);


        JPanel jPanel_version = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JLabel version = new JLabel("          Version   ", JLabel.LEFT);
        version.setForeground(new Color(236,224,13));
        version.setFont(new Font("Consolas", Font.BOLD, 25));
        JTextField version_info = new JTextField("1.0-SNAPSHOT");
        version_info.setColumns(dimension2Int(jf.getWidth()) / 30);
        version_info.setForeground(new Color(14, 145, 253));
        version_info.setFont(new Font("楷体", Font.BOLD, 20));
        jPanel_version.add(version);
        jPanel_version.add(version_info);

        JPanel jPanel_start = new JPanel(new FlowLayout());
        JButton start = new JButton("开始发布");
        start.setFont(new Font("楷体", Font.BOLD, 35));
        start.setForeground(new Color(23, 245, 82));
        jPanel_start.add(start);

        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedFile == null) {
                    JOptionPane.showMessageDialog(jf, "JAR包未选择 , 不能发布!");
                    return;
                }

                String jar_file_loc_text = selectedFile.getAbsolutePath();
                String group_text = groupId_info.getText();
                String artifactId_text = artifactId_info.getText();
                String version_text = version_info.getText();
                String idInfo = group_text + "." + artifactId_text + "." + version_text;

                JDialog start_jDialog = new JDialog();
                start_jDialog.setTitle("确认信息");
                start_jDialog.setSize(dimension2Int(jf.getWidth() * 0.7), dimension2Int(jf.getHeight() * 0.7));
                start_jDialog.setLocationRelativeTo(jf);
                start_jDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
                ImageIcon sure_icon = new ImageIcon(AddJar2LocalRepo.class.getResource("/appIcon/sure.jpg"));
                start_jDialog.setIconImage(sure_icon.getImage());

                JTextArea area = new JTextArea(dimension2Int(start_jDialog.getWidth()) / 100, dimension2Int(start_jDialog.getWidth()) / 13);
                area.setLineWrap(true);
                area.setEnabled(false);
                area.setFont(new Font("楷体", Font.BOLD, 20));
                area.setForeground(new Color(79, 77, 233));
                area.setText(idInfo);
                JScrollPane sure_jsp = new JScrollPane(area);

                JButton deploy = new JButton("  确认发布  ");
                deploy.setFont(new Font("楷体", Font.BOLD, 25));
                deploy.setForeground(new Color(79, 77, 233));
                deploy.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (!("".equals(jar_file_loc_text) || "".equals(group_text) || "".equals(artifactId_text) || "".equals(version_text))) {

                            try {
                                StringBuilder sb = new StringBuilder();
                                String[] splits = jar_file_loc_text.split("\\\\");
                                System.out.println(Arrays.toString(splits));
                                String disk_root = splits[0];
                                sb.append("copy " + jar_file_loc_text + " " + disk_root + "\\\n");
                                sb.append("mvn install:install-file ");
                                sb.append("-DgroupId=");
                                sb.append(group_text.trim());
                                sb.append(" -DartifactId=");
                                sb.append(artifactId_text);
                                sb.append(" -Dversion=");
                                sb.append(version_text);
                                sb.append(" -Dpackaging=jar");
                                sb.append(" -Dfile=");
                                copy_jar_file = disk_root + "\\" + selectedFile.getName();
                                sb.append(copy_jar_file);
//                                sb.append(selectedFile.getAbsoluteFile());

                                bat_file = new File("deployJAR.bat");
                                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(bat_file), sys_encode));
                                bw.write(sb.toString());
                                bw.flush();
                                bw.close();
                                JOptionPane.showMessageDialog(jf, "脚本写出成功");
                                int deploy = JOptionPane.showConfirmDialog(jf, "确定开始发布?");
                                // 0 是 , 1 否 , 2撤销
                                if (deploy == 0) {
                                    StringBuilder sb_callback = new StringBuilder();
                                    Process process = Runtime.getRuntime().exec("cmd /c " + bat_file.getAbsolutePath());
                                    BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), sys_encode));
                                    String line;
                                    while ((line = br.readLine()) != null) {
                                        sb_callback.append(line + "\n");
                                        System.out.println(line);
                                    }
                                    br.close();
                                    process.waitFor();
                                    if (sb_callback.toString().contains("BUILD SUCCESS")) {
                                        JOptionPane.showMessageDialog(jf, "发布成功");
                                    } else if (sb_callback.toString().contains("BUILD FAILURE")) {
                                        JOptionPane.showMessageDialog(jf, "发布失败");
                                    }

                                    area.setText("");
                                    area.setText(sb_callback.toString());

                                }

                            } catch (IOException | InterruptedException ioException) {
                                ioException.printStackTrace();
                            }


                        } else {
                            JOptionPane.showMessageDialog(start_jDialog, "坐标ID填写不完整 , 请关闭确认窗口检查!");
                        }
                    }
                });

                JPanel sure_jPanel = new JPanel();
                sure_jPanel.add(sure_jsp);
                sure_jPanel.add(deploy);
                start_jDialog.add(sure_jPanel);
                start_jDialog.setVisible(true);
            }
        });


        jPanel_title.setOpaque(false);
        jPanel_location.setOpaque(false);
        jPanel_groupId.setOpaque(false);
        jPanel_artifactId.setOpaque(false);
        jPanel_version.setOpaque(false);
        jPanel_start.setOpaque(false);

        jf.add(jPanel_title);
        jf.add(jPanel_location);
        jf.add(jPanel_groupId);
        jf.add(jPanel_artifactId);
        jf.add(jPanel_version);
        jf.add(jPanel_start);


        jf.setVisible(true);
        jf.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                int sure_close = JOptionPane.showConfirmDialog(jf, "确定关闭程序?");
                if (sure_close == 0) {
                    if (bat_file != null) {
                        JOptionPane.showMessageDialog(jf, "检测到中间文件,清理后自动关闭程序");
                        try {
                            String cls = "cmd /c del /Q " + bat_file.getAbsolutePath();
                            System.out.println(cls);
                            Process process = Runtime.getRuntime().exec(cls);
                            process.waitFor();
                            File jar_file = new File(copy_jar_file);
                            if (jar_file.exists()) {
                                boolean delete = jar_file.delete();
                                if (delete)
                                    JOptionPane.showMessageDialog(jf, "清理完成!");
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                } else {
                    return;
                }
                System.exit(0);
            }

            @Override
            public void windowClosed(WindowEvent e) {
            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
        JAR_Location.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser("./");
                fileChooser.setDialogTitle("选择JAR包");
                fileChooser.setFileFilter(new FileNameExtensionFilter("*.jar;*.zip", "zip", "jar"));
               /* fileChooser.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        String name = file.getName();
                        return file.isDirectory() || name.toLowerCase().endsWith(".jar") || name.toLowerCase().endsWith(".zip");  // 仅显示目录和xls、xlsx文件;
                    }

                    @Override
                    public String getDescription() {
                        return "*.jar;*.zip";
                    }
                });*/
//                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.showDialog(jf, "选择输出路径");
                selectedFile = fileChooser.getSelectedFile();
                if (selectedFile != null) {
                    jar_file_loc.setText(selectedFile.getAbsolutePath());
                }
//                fileChooser.setVisible(true);
//                System.out.println(selectedFile);
            }
        });
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    jf.repaint();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }

    public static int dimension2Int(double dimension) {
        return (int) dimension;
    }

}
