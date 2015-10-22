import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.swing.*;

public class StopWatch extends JPanel
{
	private Timer myTimer1;
	public static final int ONE_SEC = 1000;   //time step in milliseconds
	public static final int TENTH_SEC = 100;

	private Font myClockFont;
	private long difference;
	private Date date1;
	private Date date2;
	
	private JButton stopBtn, resetBtn, saveWorkBtn;
	private JToggleButton startBtn;
	private boolean isStartSelected;
	private JLabel timeLbl;
	private JLabel elapsedLbl;
	private JPanel topPanel, bottomPanel;

	private int clockTick;  	//number of clock ticks; tick can be 1.0 s or 0.1 s
	private double clockTime;  	//time in seconds
	private String clockTimeString;


	public StopWatch()
	{
		
		isStartSelected=false;
		
		clockTimeString = new Double(clockTime).toString();
		myClockFont = new Font("Serif", Font.PLAIN, 50);

		timeLbl = new JLabel();
		timeLbl.setFont(myClockFont);
		
		elapsedLbl = new JLabel();
		elapsedLbl.setFont(myClockFont);
		
		startBtn = new JToggleButton("Record work", false);
		startBtn.addItemListener(new ItemListener() 
		
		{
			public void itemStateChanged ( ItemEvent ie ) 
			{

				if ( startBtn.isSelected ( )==true) 
				{
					isStartSelected=true;
					System.out.println("hello");
					timeLbl.setText("Recording work");
					elapsedLbl.setText("");
					date1 = new Date();
				} else {
					isStartSelected=false;
					System.out.println("goodbye");
					timeLbl.setText("");
					date2 = new Date();
					difference = date2.getTime() - date1.getTime(); 
					//elapsedLbl.setText(String.valueOf(difference));
					elapsedLbl.setText(
							String.format("%d hour, %d min, %d sec", 
									TimeUnit.MILLISECONDS.toHours(difference),
								    TimeUnit.MILLISECONDS.toMinutes(difference),
								    TimeUnit.MILLISECONDS.toSeconds(difference) - 
								    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(difference))
								));
					
				
					
				}
				
			}

			   });
		
		saveWorkBtn = new JButton("Save work");
		
		saveWorkBtn.addActionListener(new ActionListener() {
 
            public void actionPerformed(ActionEvent e)
            {
               if(startBtn.isSelected()==true)
               {
            	   startBtn.setSelected(false);
               }
            }
        });      
		
		
		
		

	}//end of StopWatch constructor

	public void launchStopWatch()
	{
		topPanel = new JPanel();
		bottomPanel = new JPanel();
		topPanel.add(timeLbl);
		topPanel.add(elapsedLbl);
		bottomPanel.add(startBtn);
		bottomPanel.add(saveWorkBtn);

		this.setLayout(new BorderLayout());

		add(topPanel, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);

		setSize(300,200);
		setBackground(Color.orange);

	}//end of launchClock
	
void getTime()
{
	
}

public long getDifference() {
	return difference;
}

public void setDifference(long difference) {
	this.difference = difference;
}

public Date getDate1() {
	return date1;
}

public void setDate1(Date date1) {
	this.date1 = date1;
}

public Date getDate2() {
	return date2;
}

public void setDate2(Date date2) {
	this.date2 = date2;
}

public JLabel getTimeLbl() {
	return timeLbl;
}

public void setTimeLbl(JLabel timeLbl) {
	this.timeLbl = timeLbl;
}

public JLabel getElapsedLbl() {
	return elapsedLbl;
}

public void setElapsedLbl(JLabel elapsedLbl) {
	this.elapsedLbl = elapsedLbl;
}

}//end of public class

//Testing Code

class MyTestFrame extends JFrame
{
	StopWatch StopWatch1;

	public MyTestFrame()
	{
		super("My Stop Watch");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container myPane = getContentPane();

		StopWatch1 = new StopWatch();
		StopWatch1.launchStopWatch();
		myPane.add(StopWatch1);
		pack();
		setVisible(true);
	}
}