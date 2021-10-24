package NCGame;
import javax.swing.*;
import javax.swing.event.*;


import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;

public class NCGame extends JFrame {
	static Icon img[] = new ImageIcon[2];
	static labPan labpan = new labPan();
	static attackPan attackpan = new attackPan();
	static modePan modepan = new modePan();
	static btnPan btnpan = new btnPan();
	static JButton start_btn = new JButton("Game Start");
	static int btn_OX[] = new int[9];
	static int GameMode = 0; //0=與玩家對戰  1=與電腦AI對戰 
	NCGame () {
		setTitle ("井字遊戲");
		setSize (500,700);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		setLayout(null);
		for(int i=0;i<2;i++){
			//img[i] = new ImageIcon(i+".png");
//                    System.out.println(getClass().getResource("images/"+i+".png"));
			img[i] = new ImageIcon(getClass().getResource("images/"+i+".png"));
                        
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		NCGame frm = new NCGame();	
		
		start_btn.addActionListener(new startbtnListener());
		
		start_btn.setBounds(150, 610, 200, 50);
		
		frm.add(labpan);
		frm.add(attackpan);
		frm.add(modepan);
		frm.add(btnpan);
		frm.add(start_btn);
		frm.setVisible(true);
	}
	

}

class labPan extends JPanel{  //簡介panel
	static JLabel lab = new JLabel("這是一個井字遊戲", 0);
	public labPan(){
		setBounds(0, 50, 500, 50);
		add(lab);
	}
}
class attackPan extends JPanel{  //簡介panel
	static JLabel lab = new JLabel("攻擊方:", 0);
	static JLabel lab1 = new JLabel("O先攻", 0);
	public attackPan(){
		setBounds(0, 100, 500, 50);
		add(lab);
		add(lab1);
		setVisible(false);
	}
	static void setLab(int a){
		String str="";
		if(a == 0){
			str = "X";
		} else {
			str = "O";
		}
		lab1.setText("換" + str + "攻擊。");
	}
}
class modePan extends JPanel{ //遊戲模式panel
	static JLabel mode_lab = new JLabel("對戰方式:");
	static JCheckBox player = new JCheckBox("與玩家對戰",true);
	static JCheckBox computerAI = new JCheckBox("與電腦AI對戰");
	static ButtonGroup mode_grp = new ButtonGroup() ;  
	public modePan(){
		setBounds(0,100,500,50);
		mode_grp.add(computerAI);
		mode_grp.add(player);
		computerAI.addItemListener(new modeListener());
		player.addItemListener(new modeListener());
		add(mode_lab);
		add(player);
		add(computerAI);
	}
}

class btnPan extends JPanel{  //簡介panel
	static JButton btn[] = new JButton[9];
	public btnPan(){
		setLayout(new GridLayout(3,3));
		setBounds(20, 150, 450, 450);
		for(int i=0;i<9;i++){
			btn[i] = new JButton("");
			btn[i].setEnabled(false);
			btn[i].addActionListener(new gameListener());
			add(btn[i]);
		}
	}

}
class modeListener implements ItemListener{ //條件radio選擇事件處理
	public void itemStateChanged(ItemEvent e){
		if(e.getSource() == modePan.player){
			NCGame.GameMode = 0;
		} else if(e.getSource() == modePan.computerAI){
			NCGame.GameMode = 1;
		}
	}
}
class startbtnListener implements ActionListener { // 開始按鈕觸發處理事件
	public void actionPerformed(ActionEvent e) {
		NCGame.modepan.setVisible(false);
		for(int i=0;i<9;i++){
			NCGame.btn_OX[i] = -1; //將每個btn裡面代表O或X初始化
			btnPan.btn[i].setIcon(null);
			btnPan.btn[i].setEnabled(true);
		}
		gameListener.OX_now = 0;
		NCGame.start_btn.setEnabled(false);
		NCGame.attackpan.setVisible(true);
	}
}
class gameListener implements ActionListener { //九宮格按鈕觸發處理事件
	static int OX_now=0;
	static int winner=-1;

	public void actionPerformed(ActionEvent e) {
		attackPan.setLab(OX_now);
		OX_now = (++OX_now) % 2;
		for(int i=0;i<9;i++){ 
			if(e.getSource() == btnPan.btn[i]){
				btnPan.btn[i].setIcon(NCGame.img[OX_now]);
				btnPan.btn[i].setEnabled(false);
				NCGame.btn_OX[i] = OX_now;
				break;
			}
		}
		winner = getWinner();
		if(winner >= 0)
			GameOver();
		
		
		if(NCGame.GameMode == 1 && winner == -1){
			attackPan.setLab(OX_now);
			computerAI();
			winner = getWinner();
			if(winner >= 0)
				GameOver();
		}
		
		
	}
	void GameOver(){
		String winner_str = "";
		for(int i=0;i<9;i++){
			btnPan.btn[i].setEnabled(false);
		}
		if(winner == 0 )
			winner_str = "X獲得勝利";
		else if(winner == 1)
			winner_str = "O獲得勝利";
		else if(winner == 2)
			winner_str = "和局";
		JOptionPane.showConfirmDialog(null,"此局為" + winner_str + "。","獲勝訊息", JOptionPane.DEFAULT_OPTION,JOptionPane.DEFAULT_OPTION);
		NCGame.start_btn.setEnabled(true);
		NCGame.modepan.setVisible(true);
		NCGame.attackpan.setVisible(false);
	}
	void computerAI(){
		int conline[][] = {{0,1,2},{3,4,5},{6,7,8},
							{0,3,6},{1,4,7},{2,5,8},
							{0,4,8},{2,4,6}};  //連成線的所有狀態
		int haveO[] = new int[8]; //用來紀錄一排有幾個圈 ex:haveO[0] = {0,1,2}裡有幾個圈
		int haveX[] = new int[8]; //用來紀錄一排有幾個叉

		OX_now = (++OX_now) % 2;
		for(int i=0;i<8;i++) { //紀錄一排有幾個圈與叉
			int tmp_O=0,tmp_X=0;
			for(int j=0;j<3;j++){
				if(NCGame.btn_OX[conline[i][j]] == 0)
					tmp_X++;
				else if (NCGame.btn_OX[conline[i][j]] == 1)
					tmp_O++;
			}
			if((tmp_O + tmp_X) == 3)
				continue;
			haveO[i] = tmp_O;
			haveX[i] = tmp_X;
		}
		
		boolean advf = false,disadvf = false; //電腦對自己有利與不利旗標
		int adv=0,disadv=0; ////電腦對自己有利與不利的某一排
		for(int i=0;i<8;i++){
			if((haveX[i]+haveO[i]) == 3)
				continue;
			if(haveX[i] == 2 && !advf){
				adv = i;
				advf = true;
			}
			if(haveO[i] == 2 && !disadvf){
				disadv = i;
				disadvf = true;
			}
		}
		
		if((advf && disadvf) || (advf && !disadvf)){ //當電腦對自己有利
			for(int i=0;i<3;i++){
				if(NCGame.btn_OX[conline[adv][i]] == -1){
					NCGame.btn_OX[conline[adv][i]] = OX_now;
					btnPan.btn[conline[adv][i]].setIcon(NCGame.img[OX_now]);
					btnPan.btn[conline[adv][i]].setEnabled(false);
				}
			}
		} else if(disadvf){ //當電腦對自己不利
			for(int i=0;i<3;i++){
				if(NCGame.btn_OX[conline[disadv][i]] == -1){
					NCGame.btn_OX[conline[disadv][i]] = OX_now;
					btnPan.btn[conline[disadv][i]].setIcon(NCGame.img[OX_now]);
					btnPan.btn[conline[disadv][i]].setEnabled(false);
				}
			}
		} else {
			boolean f = false;
			while(!f){
				boolean tmp=false;
				for(int i=0;i<9;i++){
					if(NCGame.btn_OX[i] == -1){
						tmp = true;
					}
				}
				if(!tmp)
					break;
				
				int AI = (int)(Math.random() * 9);
				if(NCGame.btn_OX[AI] == -1){
					NCGame.btn_OX[AI] = OX_now;
					btnPan.btn[AI].setIcon(NCGame.img[OX_now]);
					btnPan.btn[AI].setEnabled(false);
					f = true;
				}
			}
		}
		
	}
	
	int getWinner() {
		boolean nopeace_flag = false;
		for(int i=0;i<2;i++){
			if(NCGame.btn_OX[0] == i && NCGame.btn_OX[1] == i  && NCGame.btn_OX[2] == i ||
			   NCGame.btn_OX[3] == i && NCGame.btn_OX[4] == i  && NCGame.btn_OX[5] == i	||
			   NCGame.btn_OX[6] == i && NCGame.btn_OX[7] == i  && NCGame.btn_OX[8] == i ||
			   NCGame.btn_OX[0] == i && NCGame.btn_OX[3] == i  && NCGame.btn_OX[6] == i ||
			   NCGame.btn_OX[1] == i && NCGame.btn_OX[4] == i  && NCGame.btn_OX[7] == i ||
			   NCGame.btn_OX[2] == i && NCGame.btn_OX[5] == i  && NCGame.btn_OX[8] == i ||
			   NCGame.btn_OX[0] == i && NCGame.btn_OX[4] == i  && NCGame.btn_OX[8] == i ||
			   NCGame.btn_OX[2] == i && NCGame.btn_OX[4] == i  && NCGame.btn_OX[6] == i){
				return i;	
			}
		}
		for(int i=0;i<9;i++){
			if(NCGame.btn_OX[i] == -1)
				nopeace_flag = true;
		}
		if(!nopeace_flag)
			return 2;
		return -1;
	}
}
