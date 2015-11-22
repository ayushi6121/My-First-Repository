# My-First-Repository
1st JAVA Graphics code
/* Adapted from code posted by R.J. Lorimer in an articleentitled "Java2D: Have Fun With Affine
     Transform". The original post and code can be found
     at http://www.javalobby.org/java/forums/t19387.html.
*/

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.BorderFactory;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.Popup;
import javax.swing.PopupFactory;


import java.applet.*;
import java.io.*;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.ArrayUtils;


class MyCode {

	String input_pattern= " ";
	public double[] freq;
	public double[] per_freq;
	Map map;
	public String null_freq="on";
	
	public int ha;
	private static final double R90 = Math.toRadians(180);
  private static final double R_90 = Math.toRadians(-180);
  
    MyCodeCanvas canvas;
    AffineTransform at;   // the current pan and zoom transform
    Point2D XFormedPoint; // storage for a transformed mouse point

  public static void main(String[] args) {
  	javax.swing.SwingUtilities.invokeLater(new Runnable() {
  		public void run() {
  		    new MyCode();
  		}
	  });
  }

   public MyCode() {
		JFrame frame = new JFrame();
		canvas = new MyCodeCanvas();
		PanningHandler panner = new PanningHandler();
		canvas.addMouseListener(panner);
		canvas.addMouseMotionListener(panner);
		canvas.setBorder(BorderFactory.createLineBorder(Color.black));

		// code for handling zooming
		JSlider zoomSlider = new JSlider(JSlider.VERTICAL, 0, 10000, 500);
		zoomSlider.setMajorTickSpacing(500);
		zoomSlider.setMinorTickSpacing(100);
		zoomSlider.setPaintTicks(true);
		zoomSlider.setPaintLabels(true);
		zoomSlider.addChangeListener(new ScaleHandler());

		JComboBox Repeats;

		JMenuBar bar = new JMenuBar();
		JMenu menu1 = new JMenu("File");
		JMenu menu2 = new JMenu("Patterns");
		JMenu menu3 = new JMenu("Null_frequency");
		JMenuItem menu1open = new JMenuItem("Open Sequence");
		JMenuItem menu1exit = new JMenuItem("Exit");
		JMenuItem menu2item;
		JMenuItem menu3on = new JMenuItem("on");
		JMenuItem menu3off = new JMenuItem("off");

		StringRead obj = new StringRead();
		try{
			//StringRead obj = new StringRead();
			map = obj.StringRead(2, "on");
		}
		catch(IOException en){
		}
		List<String> pattern = new ArrayList<String>(map.keySet());
		//String[] arr = new String[pattern.size()];
		String[] dimension = {"Di","Tri","Tetra","Penta","Hexa","Hepta","Octa","Nona","Deca"};
		Repeats =  new JComboBox(dimension);
		canvas.cal_per_freq(pattern);
		canvas.repaint();

		menu1.add(menu1open);
		menu1open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){

				canvas.repaint();
				int dim =3;
				String xa= "on";
				try{
					map = obj.StringRead(dim, xa);
				}
				catch(IOException en){
					System.out.println(en);
				}
				List<String> pattern = new ArrayList<String>(map.keySet());
				canvas.cal_per_freq(pattern);
				System.out.println("HELLO");
			}
		});

		menu1.add(menu1exit);

		menu1exit.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e){
						System.exit(0);
					}
		});



		for(int j=0; j<dimension.length;j++){
		int filenum = j+2;
		menu2item =new JMenuItem(dimension[j]);
		menu2item.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e){
						Repeats.removeAllItems();
						try{
							map = obj.StringRead(filenum, null_freq);
						}
						catch(IOException en){
							System.out.println(en);
						}
						List<String> pattern = new ArrayList<String>(map.keySet());
						//String[] arr = new String[pattern.size()];
						//Repeats = new JComboBox();
						for(String tmp : pattern){
							Repeats.addItem(tmp);
						}
						canvas.cal_per_freq(pattern);
						canvas.repaint();
						//System.out.println(pattern.size());

						//System.out.println("HELLO");
					}
			});
			menu2.add(menu2item);
		}


		menu3.add(menu3on);
		menu3on.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e){
						null_freq ="on";
						new MyCodeCanvas();
						//System.out.println("HELLO");
					}
		});
		menu3.add(menu3off);
		menu3off.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e){
						null_freq ="off";
						new MyCodeCanvas();
						//System.out.println("HELLO");
					}
		});

		bar.add(menu1);
		bar.add(menu2);
		bar.add(menu3);



		Repeats.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				canvas.repaint();
				int az=Repeats.getSelectedIndex();
				canvas.highlight(az);
			}
		});


		JLabel enterrepeats;
		enterrepeats = new JLabel("Repeat: ");
		enterrepeats.setBounds(30,50,120,20);
		Repeats.setBounds(80,50,120,20);


		// Add the components to the canvas
		//frame.getContentPane().add(table, BorderLayout.SOUTH);
		frame.getContentPane().add(enterrepeats, BorderLayout.NORTH);
		frame.getContentPane().add(Repeats, BorderLayout.NORTH);
		frame.getContentPane().add(zoomSlider, BorderLayout.EAST);
		frame.getContentPane().add(bar, BorderLayout.NORTH);
		frame.getContentPane().add(canvas, BorderLayout.CENTER);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
   }

  class MyCodeCanvas extends JComponent {
		double translateX;
		double translateY;
		double scale;

		MyCodeCanvas() {
			translateX = 0;
			translateY = 0;
			scale = 1;
		}

		public void cal_per_freq(List<String> a){

			freq = new double[a.size()];
			double sum=0;
			int i=0;
			for(String tmp_pat : a){

				List<Integer> pos = (ArrayList<Integer>)map.get(tmp_pat);
				freq[i]= pos.size();
				sum+=freq[i];
				//System.out.println(tmp_pat+"::"+freq[i]+"::"+sum);
				i++;
				//System.out.println(i);
			}
			per_freq = new double[freq.length];
			double avg = 100/sum;

			for(i=0; i<freq.length;i++){
				per_freq[i]= avg*freq[i];
				//System.out.println(avg*freq[i]+"::"+i);

			}
			//System.out.println(freq.length);
			new MyCodeCanvas();
		}

		public void highlight(int patts){
			ha = patts;
			new MyCodeCanvas();
		}

		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			// save the original transform so that we can restore
			// it later
			AffineTransform saveTransform = g2.getTransform();
    	// We need to add new transforms to the existing
			// transform, rather than creating a new transform from scratch.
			// If we create a transform from scratch, we will
			// will start from the upper left of a JFrame,
			// rather than from the upper left of our component
			at = new AffineTransform(saveTransform);

			// The zooming transformation. Notice that it will be performed
			// after the panning transformation, zooming the panned scene,
			// rather than the original scene
			at.translate(getWidth()/2, getHeight()/2);
			at.scale(scale, scale);
			at.translate(-getWidth()/2, -getHeight()/2);

			// The panning transformation
			at.translate(translateX, translateY);

			g2.setTransform(at);
			g2.translate(getWidth()/2, getHeight()/2);

			double l=0.000;

			for(int j=0;j<per_freq.length;j++) {
				if(per_freq[j] > l) {
					l = per_freq[j];
				}
			}
			int rval, gval, bval;

			double rotation = Math.toRadians(360.0 / per_freq.length);

			for(int i = 0; i < per_freq.length; i++) {

				float ab = (float)per_freq[i];
				rval = (int)Math.floor(ab * 150/l);
				gval = (int)Math.floor(ab * 200/l);
				bval = (int)Math.floor(ab * 250/l);
				//System.out.println(position[i]+"::"+rval+"::"+gval+"::"+bval);
				if(i==ha){
					g2.setColor(Color.RED);
				}
				else{
					g2.setColor(new Color(rval,gval,bval));
				}


				// fill the rectangle
				g2.translate(90, 0);
				g2.rotate(R90);
			  double x = ab*(-25);
        g2.setStroke(new BasicStroke(ab));
				// draw the line
				//g2.drawLine(0, 0, 60, 0);
				g2.drawLine(0, 0, (int)x, 0);
				g2.rotate(R_90);
				g2.translate(-90, 0);
				g2.rotate(rotation);
      }
      // make sure you restore the original transform or else the drawing
			// of borders and other components might be messed up
			g2.setTransform(saveTransform);
		}
		public Dimension getPreferredSize() {
			return new Dimension(1000, 1000);
		}
  }

  class PanningHandler implements MouseListener, MouseMotionListener {
		double referenceX;
		double referenceY;
		// saves the initial transform at the beginning of the pan interaction
		AffineTransform initialTransform;

		// capture the starting point
		public void mousePressed(MouseEvent e) {

			// first transform the mouse point to the pan and zoom
			// coordinates
			try {
				XFormedPoint = at.inverseTransform(e.getPoint(), null);
			}
			catch (NoninvertibleTransformException te) {
				System.out.println(te);
			}

			// save the transformed starting point and the initial
			// transform
			referenceX = XFormedPoint.getX();
			referenceY = XFormedPoint.getY();
			initialTransform = at;
		}

		public void mouseDragged(MouseEvent e) {

			// first transform the mouse point to the pan and zoom
			// coordinates. We must take care to transform by the
			// initial tranform, not the updated transform, so that
			// both the initial reference point and all subsequent
			// reference points are measured against the same origin.
			try {
				XFormedPoint = initialTransform.inverseTransform(e.getPoint(), null);
			}
			catch (NoninvertibleTransformException te) {
				System.out.println(te);
			}

			// the size of the pan translations
			// are defined by the current mouse location subtracted
			// from the reference location
			double deltaX = XFormedPoint.getX() - referenceX;
			double deltaY = XFormedPoint.getY() - referenceY;

			// make the reference point be the new mouse point.
			referenceX = XFormedPoint.getX();
			referenceY = XFormedPoint.getY();

			canvas.translateX += deltaX;
			canvas.translateY += deltaY;

			// schedule a repaint.
			canvas.repaint();
		}

		public void mouseClicked(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mouseMoved(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
	}

  class ScaleHandler implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			JSlider slider = (JSlider)e.getSource();
			int zoomPercent = slider.getValue();
			// make sure zoom never gets to actual 0, or else the objects will
			// disappear and the matrix will be non-invertible.
			canvas.scale = Math.max(0.00001, zoomPercent / 100.0);
			canvas.repaint();
		}
  }
}
