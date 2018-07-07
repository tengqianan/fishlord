package fishlord;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Fishlord {

	public static void main(String[] args) throws Exception {
		JFrame frame = new JFrame("捕鱼达人");
		Pool pool = new Pool();
		frame.add(pool);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 关闭窗口时关闭程序
		frame.setSize(800, 480);
		frame.setLocationRelativeTo(null);// 设置窗口居中，必须放在setSize之后
		frame.setResizable(false);// 不允许用户改变窗口大小
		frame.setVisible(true);
		pool.action();
	}
}

class Pool extends JPanel {
	BufferedImage background = null;
	Fish fish = null;;
	Fish[] fishs = new Fish[9];
	Net net = null;
	int score = 0;
	int fontsize = 20;
	Font font = new Font("楷体", Font.BOLD, fontsize);

	Pool() throws IOException {
		// background = ImageIO.read(new File("bg.jpg")); //读取工程目录图片
		background = ImageIO.read(getClass().getResourceAsStream(
				"/images/bg.jpg"));
		/**1）getClass().getResourceAsStream()方法读取的是src/images包下的图片
		 * 	2）background = ImageIO.read(new File("images/bg.jpg"));
		 * 这个方法读取的是工程CatchFish/images文件夹下的图片
		 */
       
		for (int i = 0; i < 9; i++) {
			fish = new Fish("fish0" + (i + 1));
			fishs[i] = fish;
			fish.start();
		}
	}

	public void paint(Graphics g) {  //paint什么时候调用？
		//System.out.println("paint");
		g.drawImage(background, 0, 0, null);
		for (int i = 0; i <fishs.length; i++) {
			Fish tempfish = fishs[i];
			g.drawImage(tempfish.fishimage, tempfish.x, tempfish.y, null);
		}
		if (net.show) {
			g.drawImage(net.netimage, net.x - net.width / 2, net.y - net.height/ 2, null);
		}
		g.setFont(font);
		g.setColor(Color.white);
		g.drawString("SCORE:", 10, 20);
		g.setColor(Color.red);
		g.drawString("             " + score, 10, 20);
	}

	public void action() throws Exception {
		
		net = new Net();

		MouseAdapter m = new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				net.show = true;
			}

			public void mouseExited(MouseEvent e) {
				net.show = false;
			}

			// 在鼠标移动时候执行
			public void mouseMoved(MouseEvent e) {
				// MouseEvent 鼠标事件:鼠标事件发生时间地点人物
				long time = e.getWhen();
				int x = e.getX();
				int y = e.getY();

				// Object o=e.getSource();//发生事件的物体pool
				net.x = x;
				net.y = y;
			}

			public void mousePressed(MouseEvent e) {
				catchFish();// catch:抓鱼 在鼠标按下的时候，进行抓鱼操作
			}

		};
		// 在当前方法中代表当前的 这个（this）pool对象
		this.addMouseListener(m); // 处理这个pool对象鼠标动作
		this.addMouseMotionListener(m);
		net.show = true;// 调试代码

		while (true) {
			//System.out.println("repaint");
			repaint();
			try {
				Thread.sleep(80);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected void catchFish() {
		// 鱼在不在网的范围内？在的话就让鱼消失
		for (int i = 0; i < fishs.length; i++) {
			fish = fishs[i];
			if (fish.contains(net.x, net.y)) {// 判断在不在网的范围
				fish.getOut();
				score += fish.width / 10;
			}
		}
	}

}

class Fish extends Thread {
	int x, y, index = 0, width, height, step;

	BufferedImage fishimage;
	BufferedImage[] fishimages = new BufferedImage[9];
	Random r;

	Fish(String fishname) throws IOException {
		//System.out.println("Fish()");
		for (int i = 0; i < 9; i++) {
			// BufferedImage tempfishimage = ImageIO.read(new File(fishname +
			// "_0"
			// + (i + 1) + ".png"));
			BufferedImage tempfishimage = ImageIO.read(getClass()
					.getResourceAsStream(
							"/images/" + fishname + "_0" + (i + 1) + ".png"));
			fishimages[i] = tempfishimage;
		}
		fishimage = fishimages[index];
		r = new Random();// 不写数字表示的是int范围内的一个数字
		width = fishimage.getWidth();
		height = fishimage.getHeight();
		x = 790;
		y = r.nextInt(470 - height);
		step = r.nextInt(4) + 1;
	}

	public void run() {
		while (true) {
			try {
				Thread.sleep(50);
				index++;
				fishimage = fishimages[index % fishimages.length];
				// 现在要动，所以要改变图片？300
				x = x - step;
				if (x <= 0 || y <= 0 || y >= 480)
					getOut();
			} catch (Exception e) {
			}
		}
	}

	// 检查（netx,nety）的坐标是否在鱼的范围之内
	public boolean contains(int netx, int nety) {
		int dx = netx - x;
		int dy = nety - y;
		return dx >= 0 && dx <= width && dy >= 0 && dy <= height;
	}

	void getOut() {
		Random r = new Random();
		x = 790;
		y = r.nextInt(470 - height);
		step = r.nextInt(4) + 1;
	}
}

class Net {
	// 网的位置随着鼠标指针的移动而移动
	BufferedImage netimage = null;
	int x = 0, y = 0, width, height;
	boolean show;// 是否显示当前网对象

	Net() throws Exception {
		// netimage = ImageIO.read(new File("net09.png"));

		netimage = ImageIO.read(getClass().getResourceAsStream(
				"/images/net09.png"));
		show = false;
		width = netimage.getWidth();
		height = netimage.getHeight();
	}
}