/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package login;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.RenderedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;
import static jdk.nashorn.internal.objects.NativeString.indexOf;

/**
 *
 * @author ghost
 */
public final class Test3 extends javax.swing.JFrame {
    
    private File ImageSelected,ImageSelected2 = null;
    private File[] ImageSelectedV = null;
    private int num = 0;
    private Timer t;
    private ActionListener ac;
    private int cpb;
    private int cf = 0;
    private int c=0;
    
    public Test3() {
        initComponents();
        
        jPanelResultado.setLayout(new BoxLayout(jPanelResultado, BoxLayout.X_AXIS));
        
    }
    
    public static void processFile(File file) {
        //this function process out file

        // We get HexCode from our file
        String fileHex = getCodeFileHex(file);
        // Now we started to show the found files
        showAnalyzedFiles(fileHex);
    }
    //
    public static String getCodeFileHex(File file) {
        String data = "";

        try {

            FileInputStream f2 = new FileInputStream(file);
            BufferedInputStream myBuffer = new BufferedInputStream(f2);

            while (myBuffer.available() > 0) {
                byte dato = (byte) myBuffer.read();
                data += String.format("%02X", dato);
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Test3.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Test3.class.getName()).log(Level.SEVERE, null, ex);
        }

        return data;
    }
    //
    public static Vector getHeaders(String fileHex, Vector headers) {

        Vector data = new Vector();
        int sizeFileHex = fileHex.length();
        int cH = 0;
        
        for(int v = 0;v < headers.size();v++){                        
            String text = (String) headers.elementAt(v);
            System.out.println("SCANEO nro:"+v+"-"+" para:"+text);
            cH = 0;
            int sizeText = text.length();
            for (int i = 0; i < sizeFileHex - (sizeText - 1); i++) {
                String subText = getSubString(i, sizeText, fileHex);
                if (subText.equals(text)) {
                    data.add(i);
                    cH++;                    
                }                
            }
            System.out.println("coincidencia encontrada :"+cH);
        }
        
        return data;
    }
    //
    public static String getSubString(int index, int sizeText, String fileHex) {
        String data = "";

        for (int i = 0; i < sizeText; i++) {
            data += fileHex.charAt(index + i);
        }

        return data;
    }
    //
    public static Vector listHeaders(){
        
        Vector listHeader = new Vector();
        listHeader.add("FFD8FF");   //jpg
        listHeader.add("25504446"); //pdf
        listHeader.add("494433");   //mp3        
        listHeader.add("52617221"); //rar
        listHeader.add("D0CF11E0"); //doc        
        
        return listHeader;
    }
    //
    public static Vector listHeaders2(){
        
        Vector listHeader = new Vector();
        listHeader.add("FFD8FF,jpg");    //jpg        
        listHeader.add("25504446,pdf");  //pdf
        listHeader.add("494433,mp3");    //mp3        
        listHeader.add("52617221,rar");  //rar
        listHeader.add("D0CF11E0,doc");  //doc        
        
        return listHeader;
    }    
    //
    public static Vector getSizeTypeFiles(String fileHex) {

        Vector dataHeader1 = new Vector();
        
        dataHeader1 = getHeaders(fileHex,listHeaders());
        
        System.out.println("UBICACION DE CABECERAS - INDICES");
        System.out.println(dataHeader1);
            
        Vector data2 = new Vector(dataHeader1.size());
        int indexData2 = 0;

        for (int i = 0; i < dataHeader1.size(); i++) {
            
            if (i == dataHeader1.size() - 1) {
                data2.add(dataHeader1.elementAt(i));
                data2.add(fileHex.length());
            } else {
                data2.add(dataHeader1.elementAt(i));
                data2.add((dataHeader1.elementAt(i + 1)));
            }
        }

        return data2;
    }
    //
    public static Vector function_that_takes_a_b_merges(Vector Va, Vector Vb) {
        Vector merge = new Vector();
        merge.addAll(Va);
        merge.addAll(Vb);
        return merge;
    }
    //
    public static void showAnalyzedFiles(String fileHex) {
            
        Vector bodyFIle = Case2BuildFiles(fileHex,Case2FindHeaders(fileHex));
        for(int i=0;i<bodyFIle.size();i++){
            String body = (String) bodyFIle.elementAt(i);            
            buildNewFiles(body,i);
        }        
        
    }
    //
    public static void showFiles(int index, int close, Vector sizeFiles, String fileHex) {
        
        int indexFile = (int) sizeFiles.elementAt(index);
        int closeFile = (int) sizeFiles.elementAt(close);
        int closeFileHex = fileHex.length();

        String imageCode = "";
        for (int i = indexFile; i <= closeFileHex; i++) {
            if (i == closeFileHex) {
                break;
            }
            imageCode += fileHex.charAt(i);
        }

        int accumulator = (close + 1) / 2;
        
        buildNewFiles(imageCode, accumulator);
    }
    //
    public static void buildNewFiles(String imageCode, int accumulator) {

        String header = imageCode.charAt(0)+""+
                        imageCode.charAt(1)+""+
                        imageCode.charAt(2)+""+
                        imageCode.charAt(3)+""+
                        imageCode.charAt(4)+""+
                        imageCode.charAt(5);
        
        String typeFile="";
        
        if( header.equals("FFD8FF") ) typeFile=".jpg";
        if( header.equals("255044") ) typeFile=".pdf";
        if( header.equals("494433") ) typeFile=".mp3";
        
        String[] v = imageCode.split("");
        
        String userDir = System.getProperty("user.home");        
        
        byte[] arr = new byte[imageCode.length()/2];
        
        int x = 0;

        for (int i = 0; i < (imageCode.length()); i = i + 2) {
            String val1 = imageCode.charAt(i) + "";
            String val2 = imageCode.charAt(i + 1) + "";

            String val = (val1 + val2);
            byte z = Integer.decode("0x" + val).byteValue();

            arr[x++] = z;
        }

        try (
                FileOutputStream fos = new FileOutputStream(userDir +"/Desktop/imagesResultGhost/image" + accumulator + typeFile)
            ){
            fos.write(arr);
            fos.flush();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Test3.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Test3.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelContentPane = new javax.swing.JPanel();
        jButtonProcesaImagen = new javax.swing.JButton();
        jButtonCargaImagen = new javax.swing.JButton();
        jProgressBarCargaImagen = new javax.swing.JProgressBar();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanelResultado = new javax.swing.JPanel();
        jLabelA1 = new javax.swing.JLabel();
        jButtonCargaImagen1 = new javax.swing.JButton();
        jButtonProcesaImagen1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jButtonProcesaImagen2 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanelContentPane.setBackground(new java.awt.Color(3, 18, 41));

        jButtonProcesaImagen.setFont(new java.awt.Font("Century Gothic", 0, 18)); // NOI18N
        jButtonProcesaImagen.setForeground(new java.awt.Color(173, 94, 15));
        jButtonProcesaImagen.setText("OCULTAR");
        jButtonProcesaImagen.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(173, 94, 15)));
        jButtonProcesaImagen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonProcesaImagenMouseClicked(evt);
            }
        });
        jButtonProcesaImagen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonProcesaImagenActionPerformed(evt);
            }
        });

        jButtonCargaImagen.setFont(new java.awt.Font("Century Gothic", 0, 18)); // NOI18N
        jButtonCargaImagen.setForeground(new java.awt.Color(173, 94, 15));
        jButtonCargaImagen.setText("CARGAR IMAGENES");
        jButtonCargaImagen.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(173, 94, 15)));
        jButtonCargaImagen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCargaImagenActionPerformed(evt);
            }
        });

        jProgressBarCargaImagen.setMaximum(10);

        jLabel1.setFont(new java.awt.Font("Century Gothic", 2, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(173, 94, 15));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("GHOST");

        jPanelResultado.setAutoscrolls(true);

        javax.swing.GroupLayout jPanelResultadoLayout = new javax.swing.GroupLayout(jPanelResultado);
        jPanelResultado.setLayout(jPanelResultadoLayout);
        jPanelResultadoLayout.setHorizontalGroup(
            jPanelResultadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 886, Short.MAX_VALUE)
        );
        jPanelResultadoLayout.setVerticalGroup(
            jPanelResultadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 587, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(jPanelResultado);

        jLabelA1.setBackground(new java.awt.Color(255, 153, 51));
        jLabelA1.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(173, 94, 15)));
        jLabelA1.setOpaque(true);

        jButtonCargaImagen1.setFont(new java.awt.Font("Century Gothic", 0, 18)); // NOI18N
        jButtonCargaImagen1.setForeground(new java.awt.Color(173, 94, 15));
        jButtonCargaImagen1.setText("CARGAR IMAGEN");
        jButtonCargaImagen1.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(173, 94, 15)));
        jButtonCargaImagen1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCargaImagen1ActionPerformed(evt);
            }
        });

        jButtonProcesaImagen1.setFont(new java.awt.Font("Century Gothic", 0, 18)); // NOI18N
        jButtonProcesaImagen1.setForeground(new java.awt.Color(173, 94, 15));
        jButtonProcesaImagen1.setText("EXTRAER");
        jButtonProcesaImagen1.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(173, 94, 15)));
        jButtonProcesaImagen1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonProcesaImagen1MouseClicked(evt);
            }
        });
        jButtonProcesaImagen1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonProcesaImagen1ActionPerformed(evt);
            }
        });

        jLabel2.setForeground(new java.awt.Color(255, 255, 255));

        jButtonProcesaImagen2.setFont(new java.awt.Font("Century Gothic", 0, 18)); // NOI18N
        jButtonProcesaImagen2.setForeground(new java.awt.Color(173, 94, 15));
        jButtonProcesaImagen2.setText("LIMPIAR");
        jButtonProcesaImagen2.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(173, 94, 15)));
        jButtonProcesaImagen2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonProcesaImagen2MouseClicked(evt);
            }
        });
        jButtonProcesaImagen2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonProcesaImagen2ActionPerformed(evt);
            }
        });

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanelContentPaneLayout = new javax.swing.GroupLayout(jPanelContentPane);
        jPanelContentPane.setLayout(jPanelContentPaneLayout);
        jPanelContentPaneLayout.setHorizontalGroup(
            jPanelContentPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelContentPaneLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(276, 276, 276))
            .addGroup(jPanelContentPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelContentPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelContentPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(jPanelContentPaneLayout.createSequentialGroup()
                            .addComponent(jButtonCargaImagen, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(347, 347, 347)
                            .addComponent(jButtonCargaImagen1, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jProgressBarCargaImagen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanelContentPaneLayout.createSequentialGroup()
                            .addGroup(jPanelContentPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 384, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(18, 18, 18)
                            .addGroup(jPanelContentPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jButtonProcesaImagen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButtonProcesaImagen1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButtonProcesaImagen2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabelA1, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(99, Short.MAX_VALUE))
        );
        jPanelContentPaneLayout.setVerticalGroup(
            jPanelContentPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelContentPaneLayout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelContentPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonCargaImagen, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonCargaImagen1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelContentPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanelContentPaneLayout.createSequentialGroup()
                        .addComponent(jButtonProcesaImagen2, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(33, 33, 33)
                        .addComponent(jButtonProcesaImagen, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonProcesaImagen1))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jLabelA1, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBarCargaImagen, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelContentPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelContentPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCargaImagenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCargaImagenActionPerformed
        jPanelResultado.removeAll();
        
        String userDir = System.getProperty("user.home");
        JFileChooser file = new JFileChooser(userDir +"/Desktop");        
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG","jpg");        
        file.setFileSelectionMode(JFileChooser.FILES_ONLY);                
        file.setFileFilter(filter);
        file.setMultiSelectionEnabled(true);
        
        int val = file.showOpenDialog(this);
        
        if( val == JFileChooser.APPROVE_OPTION){            
            ImageSelectedV = file.getSelectedFiles();
            
            for(int i=0;i<ImageSelectedV.length;i++){                    
                
                System.out.println(ImageSelectedV[i].getName()+"-"+
                                   ImageSelectedV[i].length()+"-"+
                                   ImageSelectedV[i].toString().substring(0,5) );
                                                
                ImageIcon img = new ImageIcon(ImageSelectedV[i].toString());                
                Icon icon = new ImageIcon(img.getImage().getScaledInstance(jLabelA1.getWidth()-20,jLabelA1.getHeight()-20, Image.SCALE_DEFAULT));  
                                
                
                JLabel contImagen = new JLabel();
                contImagen.setIcon(icon);
                
                System.out.println("ImageIcon--->"+img);
                System.out.println("icon2------->"+img.getImage());
                System.out.println("icon-------->"+icon);
                //System.out.println("label------->"+contImagen.getText().substring(0,5));
                
                jPanelResultado.add(contImagen);
                jPanelResultado.setBackground(Color.red);
                
                                
            }
                                    
            jPanelContentPane.updateUI();
            jPanelContentPane.repaint();
            jPanelContentPane.validate();
            
            this.repaint();            
        
        }else{
            message("No selecciono imagenes");
        }
    }//GEN-LAST:event_jButtonCargaImagenActionPerformed
    
    private void message(String msg){
        JOptionPane.showMessageDialog(
                null,
                msg
        );
    }
    
    private void jButtonProcesaImagenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonProcesaImagenActionPerformed
        jProgressBarCargaImagen.setStringPainted(true);
        jProgressBarCargaImagen.setValue(0);
        jLabel2.setText("Espere mientras se procesan las imagenes ");
        
        if(ImageSelectedV != null){
            if(ImageSelected2 != null){
                String userDir = System.getProperty("user.home");
                File direc = new File(userDir + "/Desktop/imagesResultGhost");
                direc.mkdir();
                                
                cpb = 0;
                ac = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cpb++;
                        jProgressBarCargaImagen.setValue(cpb);
                        jProgressBarCargaImagen.setString("cargando..."+(cpb*10)+"%");
                        
                        if (jProgressBarCargaImagen.getValue() == 3) {
                            long startTime = System.currentTimeMillis();
                            buildingImagesEmbedded();
                            long endTime = System.currentTimeMillis() - startTime;
                            System.out.println("tiempo:"+(endTime)  );
                        }
                        if (jProgressBarCargaImagen.getValue() == 10) {
                            jLabel2.setText("LA IMAGEN FUE OCULTADA CON [EXITO]");
                            t.stop();
                        }
                        
                        
                    }
                };
                t = new Timer(100, ac);
                t.start();
                
            }else{
                message("No hay imagen para ocultar");
            }
        }else{
            message("No hay imagenes seleccionadas");
        }
            
        
        
             
    }//GEN-LAST:event_jButtonProcesaImagenActionPerformed
    
    private void jButtonProcesaImagenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonProcesaImagenMouseClicked
        
    }//GEN-LAST:event_jButtonProcesaImagenMouseClicked

    private void jButtonCargaImagen1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCargaImagen1ActionPerformed
        String userDir = System.getProperty("user.home");
        JFileChooser file = new JFileChooser(userDir +"/Desktop");        
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG","jpg");        
        file.setFileSelectionMode(JFileChooser.FILES_ONLY);                
        file.setFileFilter(filter);
        file.setMultiSelectionEnabled(false);
        
        int val = file.showOpenDialog(this);
        
        if( val == JFileChooser.APPROVE_OPTION){
            ImageSelected2 = file.getSelectedFile();        
            ImageIcon img = new ImageIcon(ImageSelected2.toString());                
            Icon icon = new ImageIcon(img.getImage().getScaledInstance(jLabelA1.getWidth(),jLabelA1.getHeight(), Image.SCALE_DEFAULT));        
            jLabelA1.setIcon(icon);      
        }else{
            System.out.println("NO SELECCIONO NINGUNA IMAGEN");
        }
    }//GEN-LAST:event_jButtonCargaImagen1ActionPerformed

    private void jButtonProcesaImagen1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonProcesaImagen1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonProcesaImagen1MouseClicked

    private void jButtonProcesaImagen1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonProcesaImagen1ActionPerformed
        
        jProgressBarCargaImagen.setStringPainted(true);
        
        jLabelA1.setText("");
        //jPanelResultado.removeAll();
        jProgressBarCargaImagen.setValue(0);        
        jPanelContentPane.updateUI();
        
        jLabel2.setText("Espere mientras se procesan las imagenes ");        
        String body = "";
        String value = "";
        String valueF = "";                     
        String userDir = System.getProperty("user.home");
        String sDirectorio = userDir +"/Desktop/imagesResultGhost";               
        File f = new File(sDirectorio);        
        File[] ficheros;        
        int sizeImages = ImageSelectedV.length;
        c=0;
        
        for (int i = 0; i < sizeImages; i++) {
            body = getCodeFileHex(ImageSelectedV[i]);             
            if (body.contains("ABCDEF")) {                                
                int iniImage = body.lastIndexOf("ABCDEF");                
                valueF = body.charAt(iniImage + 7) + "";                
                c = Integer.parseInt(valueF);                
            }
        }
                
        Vector getHeader = new Vector(c);
        
        for(int i=0;i<getHeader.capacity();i++){            
            getHeader.add(i,"vacio");
        }
        
        for (int i = 0; i < sizeImages; i++) {
            body = getCodeFileHex(ImageSelectedV[i]);                    
            String subBody = "";
            int val=0;            
            if (body.contains("ABCDEF")) {                                
                int iniImage = body.lastIndexOf("ABCDEF");                                
                value = body.charAt(iniImage + 6) + "";
                valueF = body.charAt(iniImage + 7) + "";                 
                val = Integer.parseInt(value);
                c = Integer.parseInt(valueF);                
                subBody = body.substring(iniImage + 8, body.length());                
                getHeader.setElementAt(subBody,val);
            }
        }
        
        cf = 0;
        
        for(int i=0;i<getHeader.size();i++){            
            if( !((String)(getHeader.elementAt(i))).equals("vacio") ){
                cf++;
            }
        }
        
        
        
        cpb = 0;
        ac = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cpb++;
                jProgressBarCargaImagen.setValue(cpb);
                jProgressBarCargaImagen.setString("cargando..." + (cpb * 10) + "%");

                if (jProgressBarCargaImagen.getValue() == 3) {                    
                    if (cf != c) {
                        message("Faltan   imagenes");
                    } else {
                        String internImage = "";
                        for (int i = 0; i < getHeader.size(); i++) {
                            String x = (String) getHeader.elementAt(i);
                            internImage += x;
                        }
                        buildNewFiles(internImage, 100);
                        ImageIcon img = new ImageIcon(userDir + "/Desktop/imagesResultGhost/image100.jpg");
                        Icon icon = new ImageIcon(img.getImage().getScaledInstance(jLabelA1.getWidth(), jLabelA1.getHeight(), Image.SCALE_DEFAULT));
                        jLabelA1.setIcon(icon);
                    }                    
                }
                if (jProgressBarCargaImagen.getValue() == 10) {
                    jLabel2.setText("LA IMAGEN FUE EXTRAIDA CON [EXITO]");
                    t.stop();
                }

            }
        };
        t = new Timer(500, ac);
        t.start();
        
        
        
        
        /*
        if(cf!=c){
            message("Faltan   imagenes");
        }else{            
            String internImage = "";
            for (int i = 0; i < getHeader.size(); i++) {
                String x = (String) getHeader.elementAt(i);
                internImage += x;
            }
            buildNewFiles(internImage, 100);
            ImageIcon img = new ImageIcon(userDir + "/Desktop/imagesResultGhost/image100.jpg");
            Icon icon = new ImageIcon(img.getImage().getScaledInstance(jLabelA1.getWidth(), jLabelA1.getHeight(), Image.SCALE_DEFAULT));
            jLabelA1.setIcon(icon);
        }
        */
        
        
    }//GEN-LAST:event_jButtonProcesaImagen1ActionPerformed

    private void jButtonProcesaImagen2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonProcesaImagen2MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonProcesaImagen2MouseClicked

    private void jButtonProcesaImagen2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonProcesaImagen2ActionPerformed
        jPanelResultado.removeAll();
        jProgressBarCargaImagen.setValue(0);
        jProgressBarCargaImagen.setString("");
        jLabel2.setText("");
        jLabelA1.setText("");
        jLabelA1.setIcon(null);
        jPanelContentPane.updateUI();
    }//GEN-LAST:event_jButtonProcesaImagen2ActionPerformed
    
    private void extract(){
        
    }
    
    private Vector splitImage(){
        
        Vector bodyImage = new Vector();
        
        String image = getCodeFileHex(ImageSelected2);
        int totalPartitions = (ImageSelectedV.length);
        
        int sizeImage = image.length();
        int modifidPartition = 0;
        int originalPartition = 0;        
        int x = 0;
        int y = 0;
        String subCode = "";
        
        System.out.println("size-messageHide:"+sizeImage);
        
        originalPartition = (image.length()) / totalPartitions ;        
        if(originalPartition%2 != 0) 
            modifidPartition = originalPartition+1; 
        else 
            modifidPartition = originalPartition;
                        
        for(int i=1;i<=totalPartitions;i++){
            x = y;
            y = y + modifidPartition;
            
            if (i == totalPartitions) {
                y = sizeImage;
            }
            subCode = image.substring(x,y);
            bodyImage.add(subCode);
            System.out.println(i + ",x:" + x + ",y:" + y);
            
        } 
        return bodyImage;
    } 
    
    private Vector embeddedImages(){
        Vector imagesEmbedded = new Vector();
                
        int sizeImages = ImageSelectedV.length;
        String body = "";
        
        for(int i=0;i<sizeImages;i++){
            body = getCodeFileHex(ImageSelectedV[i]);
            body = body+("ABCDEF"+i+sizeImages)+(splitImage().elementAt(i));
            imagesEmbedded.add(body);
        }
        
        return imagesEmbedded;
    }
    
    private void buildingImagesEmbedded(){        
        for(int i=0;i<embeddedImages().size();i++){
            buildNewFiles((String) embeddedImages().elementAt(i),i);
        }
    }
    
    private void ListImages(){
        String userDir = System.getProperty("user.home");
        String sDirectorio = userDir +"/Desktop/imagesResultGhost";
               
        File f = new File(sDirectorio);        
        File[] ficheros;
        
        if ( f.exists() ) {
            ficheros = f.listFiles();
            for (int x = 0; x < ficheros.length; x++) {
                                
                System.out.println(ficheros[x].getName()+"-"+
                                   ficheros[x].length()+"-"+
                                   ficheros[x].toString().substring(0,5) );
                                
                ImageIcon img = new ImageIcon(ficheros[x].toString());                
                Icon icon = new ImageIcon(img.getImage().getScaledInstance(jLabelA1.getWidth()-20,jLabelA1.getHeight()-20, Image.SCALE_DEFAULT));  
                
                JLabel contImagen = new JLabel();
                contImagen.setIcon(icon);
                
                jPanelResultado.setBackground(Color.yellow);
                jPanelResultado.add(contImagen);
                jPanelResultado.repaint();                
                jPanelResultado.updateUI();
            }        
        }
        else {
            System.out.println("el directorio no existe");
        }   
    }
    
    public class Imagen extends javax.swing.JPanel{

        private ImageIcon image;

        public Imagen(Dimension d, ImageIcon img) {
            setSize(d);
            image = img;
        }

        public void paint(Graphics grafico) {
            Dimension tamaño = getSize();
            //ImageIcon img = new ImageIcon(getClass().getResource("/images/animales_2.jpg"));
            grafico.drawImage(this.image.getImage(), 0, 0, tamaño.width, tamaño.height, null);
            setOpaque(false);
            super.paintComponent(grafico);
        }
    }
    
    public static Vector Case2FindHeaders(String fileHex){
        Vector positions = new Vector();
        int value = 0;
        int c=0;
        
        for(int i=0;i<fileHex.length();i++){            
            value = fileHex.indexOf("FFD8",i);            
            if(value!=-1){                
                i = value;
                positions.add(value);                            
            }            
        }        
        positions.add(fileHex.length());        
        
        return positions;
    }
    
    public static Vector Case2BuildFiles(String fileHex,Vector posHeadres){
        Vector NewFiles = new Vector();
                
        for(int i=0;i< posHeadres.size()-1 ;i++){            
            int posHeader = (int) posHeadres.elementAt(i);
            int posFooter = (int) posHeadres.elementAt(i+1);            
            String newFile = fileHex.substring(posHeader,posFooter);            
            NewFiles.add(newFile);
        }
        
        return NewFiles;
    }
    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Test3.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Test3.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Test3.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Test3.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Test3().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCargaImagen;
    private javax.swing.JButton jButtonCargaImagen1;
    private javax.swing.JButton jButtonProcesaImagen;
    private javax.swing.JButton jButtonProcesaImagen1;
    private javax.swing.JButton jButtonProcesaImagen2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabelA1;
    private javax.swing.JPanel jPanelContentPane;
    private javax.swing.JPanel jPanelResultado;
    private javax.swing.JProgressBar jProgressBarCargaImagen;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}


