import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.microsoft.tfs.core.clients.workitem.WorkItem;
import com.microsoft.tfs.core.clients.workitem.WorkItemClient;
import com.microsoft.tfs.core.clients.workitem.project.Project;
import com.microsoft.tfs.core.clients.workitem.project.ProjectCollection;
import com.microsoft.tfs.core.clients.workitem.query.WorkItemCollection;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

public class SaveView extends JFrame {

	private JPanel contentPane;
	private JTextField timeWorkedField;
	private JTextField newWorkRemainingField;
	private JButton btnSave;
	private TimeManager timeO;
	private ProjectCollection tfsProjects;

	/**
	 * Create the frame.
	 */
	public SaveView(TimeManager time, ProjectCollection TFSProjects) {
		
		timeO = time;
		tfsProjects = TFSProjects;
		setTitle("Save Work");
//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 490, 122);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JPanel timeFieldsPanel = new JPanel();
		
		JLabel timeWorkedLabel = new JLabel("Time Worked");
		timeFieldsPanel.add(timeWorkedLabel);
		
		timeWorkedField = new JTextField();
		
		timeWorkedField.setText(String.valueOf(calculateRecordedTime()));
		timeFieldsPanel.add(timeWorkedField);
		timeWorkedField.setColumns(10);
		
		JButton btnIncrease = new JButton("Increase");
		btnIncrease.addActionListener(new incrementButton());
		timeFieldsPanel.add(btnIncrease);
		
		JButton btnDecrease = new JButton("Decrease");
		btnDecrease.addActionListener(new decrementButton());
		timeFieldsPanel.add(btnDecrease);
		
		btnSave = new JButton("Save");
		btnSave.addActionListener(new saveButton());
		contentPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		contentPane.add(timeFieldsPanel);
		contentPane.add(btnSave);
		
		JLabel lblNewRemainingWork = new JLabel("New Remaining Work:");
		contentPane.add(lblNewRemainingWork);
		
		newWorkRemainingField = new JTextField();
		newWorkRemainingField.setText(String.valueOf(calculateNewWorkRemaining()));
		contentPane.add(newWorkRemainingField);
		newWorkRemainingField.setColumns(10);
	}

	public String getTimeWorkedField() {
		return timeWorkedField.getText();
	}
	
	//Calculates the amount of time the time object was recorded for
	public Double calculateRecordedTime()
	{
		
		long difference = timeO.endDate.getTime() - timeO.startDate.getTime(); 
		
		String workRemaining = timeO.workRemaining;
		
		double recordedTime = (double) TimeUnit.MILLISECONDS.toHours(difference)+((double)TimeUnit.MILLISECONDS.toMinutes(difference)/10)+((double)(TimeUnit.MILLISECONDS.toSeconds(difference) - 
			    (double)TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(difference)))/1000);
				
		double workRem = Double.parseDouble(workRemaining);
				
		return recordedTime;
	}

	
	public Double calculateNewWorkRemaining()
	{
		double newWorkRemaining = Double.parseDouble(timeO.workRemaining)-calculateRecordedTime();
		
		return newWorkRemaining;
	}
	
	public void saveRemainingWork(WorkItem workItem){
		workItem.open();
		
		workItem.getFields().getField("Remaining Work").setValue(String.valueOf(newWorkRemainingField.getText()));
		System.out.println("Remaining Work: "+String.valueOf(workItem.getFields().getField("Remaining Work").getValue()));
		
		workItem.save();
	}

	public void setTimeWorkedField(String newField) {
		this.timeWorkedField.setText(newField);
	}

	public String getNewWorkRemainingField() {
		return newWorkRemainingField.getText();
	}

	public void setNewWorkRemainingField(String newField) {
		this.newWorkRemainingField.setText(newField);
	}
	
	//Retrieves the work item associated with a particular time object
	public WorkItem getWorkItem(TimeManager timeItem)
	{
		 //Get project ID
        Project project = tfsProjects.get(timeItem.projectName);
        
        //Create a WorkItemClient
        WorkItemClient wicClient = project.getWorkItemClient();
	       
        // Define the WIQL query.  
       String wiqlQuery = "Select ID, Title, Microsoft.VSTS.Scheduling.RemainingWork from WorkItems where (System.TeamProject = '"+project.getName()+"') order by Title";
	       
       // Run the query and get the results.  
       WorkItemCollection workItems = wicClient.query(wiqlQuery);
//       System.out.println("Work Item Size after query: "+workItems.size());
//       System.out.println("Parent project ID: "+Integer.toString(project.getID()));
//       projectIDField.setText(Integer.toString(project.getID()));
       
       System.out.println("Task Name: "+timeItem.workItemName);
       System.out.println("Work Item Collection Size:"+workItems.size());
       
       WorkItem wkItem = null;
       
       for (int i = 0; i < workItems.size(); i++)
       {	
//    	   System.out.println("There are: "+workItems.size()+" work items.");
    	   if(workItems.getWorkItem(i).getTitle().equals(timeItem.workItemName))
    	   {
    		   wkItem=(workItems.getWorkItem(i));
    		   //System.out.println("Found a task match");
			   System.out.println("Work Item ID: "+workItems.getWorkItem(i).getID());
    		   
    	   }

       }
       
       return wkItem;
	}
	
	class saveButton implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			saveRemainingWork(getWorkItem(timeO));
		}
		
	}
	
	//Can be used to increment the number of hours spent working
	class incrementButton implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			double timeWk = Math.round(Double.parseDouble(timeWorkedField.getText())*100.0)/100;
			double newTimeWk =  Math.round(Double.parseDouble(newWorkRemainingField.getText())*100.0)/100;
			
			timeWk=timeWk+1.0;
			//System.out.println(timeWk);
			
			newWorkRemainingField.setText(String.valueOf(newTimeWk-1));
			timeWorkedField.setText(String.valueOf(timeWk));
			
		}
		
	}
	
	//Can be used to decrement the number of hours spent working
	class decrementButton implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			double timeWk = Double.parseDouble(timeWorkedField.getText());
			double newTimeWk = Double.parseDouble(newWorkRemainingField.getText());
			
			timeWk=timeWk-1.0;
			//System.out.println(timeWk);
			
			newWorkRemainingField.setText(String.valueOf(newTimeWk+1));
			timeWorkedField.setText(String.valueOf(timeWk));
			
		}
		
	}

}
