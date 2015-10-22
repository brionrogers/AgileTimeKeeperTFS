import java.awt.EventQueue;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.Border;

import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EventListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JTree;

import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.border.CompoundBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.microsoft.tfs.core.TFSTeamProjectCollection;
import com.microsoft.tfs.core.clients.workitem.WorkItem;
import com.microsoft.tfs.core.clients.workitem.WorkItemClient;
import com.microsoft.tfs.core.clients.workitem.fields.FieldCollection;
import com.microsoft.tfs.core.clients.workitem.project.Project;
import com.microsoft.tfs.core.clients.workitem.project.ProjectCollection;
import com.microsoft.tfs.core.clients.workitem.query.WorkItemCollection;
import com.microsoft.tfs.core.config.IllegalConfigurationException;
import com.microsoft.tfs.core.httpclient.Credentials;
import com.microsoft.tfs.core.httpclient.UsernamePasswordCredentials;

import net.miginfocom.swing.MigLayout;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.Component;
import javax.swing.Box;
import javax.swing.JSplitPane;

public class MainView  {
	
	private JFrame	projectsFrame;
	private JPanel projectsPanel;
	private JLabel statusLabel;
	
	private ProjectCollection TFSProjects;
	private WorkItemCollection workItems;
	private TFSTeamProjectCollection tpc;
	
	private boolean hasTreeBeenInitialized;
	
	private LinkedList<TimeManager> timeList;
	
	private JTextField workItemNameField;
	private JLabel workItemNameLabel;
	
	private boolean hasProjectNameBeenInitialized;
	
	private long difference;
	private Date date1;
	private Date date2;
	
	private JMenuBar menuBar;
	private JPanel fieldsPanel;
	private JLabel lblProjectName;
	private JTextField projectNameField;
	private JLabel lblProjectId;
	private JTextField projectIDField;
	private JLabel lblWorkRemaining;
	private JTextField workRemainingField;
	private JTree projectsTree;
	private JPanel panel;
	private JPanel stopWatchPanel;
	private JMenu mnNewMenu;
	private JMenu itemTrackMenu;
	private DefaultMutableTreeNode root;
	private JToggleButton recordBtn;
	private JButton saveBtn,pauseBtn;
	
	/**
	 * Initialize the contents of the projectsFrame.
	 */
	MainView(LoginTFS tfsLogin) {
		
		//Set Tree View var false
		hasTreeBeenInitialized = false;
		
		//Initialize list of items being tracked
		timeList = new LinkedList<TimeManager>();
		
		//Create a new TFS project collection
		tpc = new TFSTeamProjectCollection(tfsLogin.tfsURI,tfsLogin.loginCredentials);
		
		//Attempt to get a work item collection
		try {
		TFSProjects = tpc.getWorkItemClient().getProjects();
		} catch (IllegalConfigurationException i)
      	 {
      		 //i.printStackTrace();
      		 System.out.println("Bad URI");
      		 JFrame frame = new JFrame();
      		JOptionPane.showMessageDialog(frame,
      			    "The supplied URI is not valid. Please contact your TFS administrator for more information.");
      	 }
		
		/**
		 * GUI Construction:
		 * Defines and creates the GUI used
		 */
		initGUI();
		
		
		recordBtn.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent actionEvent) {
			    	//Get the selected node
		              DefaultMutableTreeNode node = (DefaultMutableTreeNode) projectsTree.getLastSelectedPathComponent();
		              
						
						if(!getProjectNameField().equals("") && !getWorkRemainingField().equals("") && hasTreeBeenInitialized==true )
						{
							if ( recordBtn.isSelected ( )==true) 
							{
								if(getProjectNameField()!="" )
								{
									statusLabel.setText("Recording Work");
									
									if(searchJMenu(getWorkItemNameField(), itemTrackMenu)==false)
									{
										JMenuItem item = new JMenuItem(getWorkItemNameField());
										item.addActionListener(new MenuActionListener());
										itemTrackMenu.add(item);
										//elapsedLbl.setText("");
										date1 = new Date();
									}

//									System.out.println(getProjectNameField());
//									System.out.println(getProjectIDField());
//									System.out.println(getWorkRemainingField());
//									System.out.println(getWorkItemNameField());
									
									//Get the selected node
					                TreePath path = projectsTree.getEditingPath();
									
					                //Create a time manager
					                TimeManager time = new TimeManager(getProjectNameField(), getProjectIDField(), getWorkRemainingField(), getWorkItemNameField(),date1,path);
									timeList.add(time);
									
									
									
								}
							} else {
								statusLabel.setText("");
								//System.out.println("goodbye");
								
								date2 = new Date();
								difference = date2.getTime() - date1.getTime(); 
								statusLabel.setText(String.valueOf(difference));
								statusLabel.setText(
										String.format("%d hour, %d min, %d sec", 
												TimeUnit.MILLISECONDS.toHours(difference),
											    TimeUnit.MILLISECONDS.toMinutes(difference),
											    TimeUnit.MILLISECONDS.toSeconds(difference) - 
											    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(difference))
											));
								
							
								//System.out.println("Either the node isn't selected or the project name is empty or the remaining work is empty");
								recordBtn.setSelected(false);
							}
						} else {
							recordBtn.setSelected(false);
						}
						stopWatchPanel.revalidate();
					}
			      
			    });
	}
	
	/**
	 * Miscellaneous methods
	 */
	public void initGUI()
	{
		//Instantiate main viewing window
				projectsFrame = new JFrame("Projects List");
				//projectsFrame.getContentPane().setPreferredSize(new Dimension(500,500));
				projectsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				projectsFrame.getContentPane().setLayout(new BorderLayout(0, 0));
				
				//Instantiate a panel to hold the projectsTree
				//projectsTree displays the projects and associated
				//work items stored on the TFS server.
				projectsPanel = new JPanel();
				projectsFrame.getContentPane().add(projectsPanel);
				
				//Create the root node for the tree
		        root = new DefaultMutableTreeNode("Projects");
				
		        //Create the child nodes of the root node
		        //Creates a node for every project, creates leaves for every work item in a project
		        initTree();
		        
		        //Create a GUI tree to hold the logical tree
		        projectsTree = new JTree(root);
		        projectsTree.addTreeSelectionListener(new TreeSelectionListener());
		        
		        
		        projectsFrame.getContentPane().add(new JScrollPane(projectsTree));
		        System.out.println(""); 
				
				fieldsPanel = new JPanel();
				projectsFrame.getContentPane().add(fieldsPanel, BorderLayout.EAST);
				fieldsPanel.setLayout(new BorderLayout(0, 0));
				
				panel = new JPanel();
				fieldsPanel.add(panel, BorderLayout.NORTH);
				
				lblProjectName = new JLabel("Project Name:");
				panel.add(lblProjectName);
				
				projectNameField = new JTextField();
				projectNameField.setEditable(false);
				panel.add(projectNameField);
				projectNameField.setColumns(10);
				
				lblProjectId = new JLabel("Work Item ID:");
				panel.add(lblProjectId);
				
				projectIDField = new JTextField();
				projectIDField.setEditable(false);
				panel.add(projectIDField);
				projectIDField.setColumns(10);
				
				lblWorkRemaining = new JLabel("Work Remaining:");
				panel.add(lblWorkRemaining);
				
				workRemainingField = new JTextField();
				workRemainingField.setEditable(false);
				panel.add(workRemainingField);
				workRemainingField.setColumns(10);
				
				workItemNameLabel = new JLabel("Work Item Name:");
				panel.add(workItemNameLabel);
				
				workItemNameField = new JTextField();
				workItemNameField.setEditable(false);
				panel.add(workItemNameField);
				workItemNameField.setColumns(20);
				
				recordBtn = new JToggleButton("Record Work");
				//recordBtn.addItemListener(new ItemListener());
				saveBtn = new JButton("Save Work");
				saveBtn.addActionListener(new saveListener());
				stopWatchPanel = new JPanel();
				
				pauseBtn = new JButton("Pause Recording");
				
				stopWatchPanel.add(recordBtn);
				stopWatchPanel.add(pauseBtn);
				stopWatchPanel.add(saveBtn);
				
				fieldsPanel.add(stopWatchPanel);
				
				JLabel lblProjectList = new JLabel("Project List");
				projectsFrame.getContentPane().add(lblProjectList, BorderLayout.NORTH);
				
				JMenuBar menuBar = new JMenuBar();
				projectsFrame.setJMenuBar(menuBar);
				
				JMenu mnNewMenu = new JMenu("File");
				menuBar.add(mnNewMenu);
				
				JMenuItem mntmStartClock = new JMenuItem("Start Clock");
				mnNewMenu.add(mntmStartClock);
				
				projectsFrame.pack();
				
				menuBar = new JMenuBar();
				projectsFrame.setJMenuBar(menuBar);
				
				mnNewMenu = new JMenu("File");
				menuBar.add(mnNewMenu);
				
				itemTrackMenu = new JMenu("Items Being Tracked");
				menuBar.add(itemTrackMenu);
				
				statusLabel = new JLabel();
				fieldsPanel.add(statusLabel, BorderLayout.SOUTH);
				projectsFrame.setVisible(true);
	}
	
	public void initTree()
	{
		//Create the child nodes of the root node
        for (Project project1 : TFSProjects)
        {	
        	//Create a node for the tree to be displayed
            DefaultMutableTreeNode projectName = new DefaultMutableTreeNode(project1.getName());
            //System.out.println(project1.getName());
            
            //Create a workItemClient
            WorkItemClient workItemClient = project1.getWorkItemClient();
            
            // Define the WIQL query. The WIQL query is used to retrieve
            // data from the Work Item Client.
	        String wiqlQuery = "Select ID, Title, Microsoft.VSTS.Scheduling.RemainingWork from WorkItems where (System.TeamProject = '"+project1.getName()+"') order by Title";

	        // Run the query and get the results.  
	        workItems = workItemClient.query(wiqlQuery);
            //System.out.println(workItems.size());
            
	        //Create the children of child nodes to be displayed
	        for(int i = 0; i < workItems.size(); i++)
            {
	        	WorkItem workItem = workItems.getWorkItem(i);
	        	//System.out.println(workItem.getTitle());
            	projectName.add(new DefaultMutableTreeNode(workItem.getTitle()));
            }
	        
	        //add the child nodes to the root node
            root.add(projectName);
        }
		
	}
	
	public WorkItem getWorkItem(TimeManager timeItem)
	{
		 //Get project ID
        Project project = TFSProjects.get(timeItem.projectName);
        
        //Create a WorkItemClient
        WorkItemClient wicClient = project.getWorkItemClient();
	       
        // Define the WIQL query.  
       String wiqlQuery = "Select ID, Title, Microsoft.VSTS.Scheduling.RemainingWork from WorkItems where (System.TeamProject = '"+project.getName()+"') order by Title";
	       
       // Run the query and get the results.  
       WorkItemCollection workItems = wicClient.query(wiqlQuery);
       System.out.println("Work Item Size after query: "+workItems.size());
       System.out.println("Parent project ID: "+Integer.toString(project.getID()));
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
    		   workItemNameField.setText(workItems.getWorkItem(i).getTitle());
    		   projectIDField.setText(Integer.toString(workItems.getWorkItem(i).getID()));
			   System.out.println("Work Item ID: "+workItems.getWorkItem(i).getID());
    		   
    	   }

       }
       
       return wkItem;
	}
	
	public boolean searchJMenu(String text, JMenu menu)
	{
		for(int i = 0; i < menu.getItemCount(); i++)
		{
			if(menu.getItem(i).getText().equals(text))
			{
				return true;
			}
		}
		return false;
	}
	
	public TimeManager searchLinkedList(String taskName, LinkedList<TimeManager> timeObjectList) 
	{
		TimeManager timeManager = null;
		
		for(int i=0; i < timeObjectList.size(); i++)
		{
			if(timeObjectList.get(i).workItemName.equals(taskName))
			{
				timeManager=timeObjectList.get(i);
			}
		}
		
		return timeManager;
	}
	
	/**
	 * Listener Classes
	 * @author Development
	 *
	 */
	//JMenu listener
	class MenuActionListener implements ActionListener {
		  public void actionPerformed(ActionEvent e) {
		    System.out.println("Selected: " + e.getActionCommand());
		    
		    //getWorkItem(e.getActionCommand());
		    projectsTree.setSelectionPath(searchLinkedList(e.getActionCommand(), timeList).path);

		    
		    

		  }
		}
	
	//JTree listener
	class TreeSelectionListener implements javax.swing.event.TreeSelectionListener
    {
        public void valueChanged(TreeSelectionEvent e) 
        {	
        	//Indicates the user has clicked the tree for the first time
        	hasTreeBeenInitialized = true;
        	
        	//Get the selected node
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) projectsTree.getLastSelectedPathComponent();
            String name = node.getUserObject().toString();
                 	
            
            //Check if node is leaf
            if(node.isLeaf()==true)
            {
//	            /* retrieve the node that was selected */ 
//	                Object nodeInfo = node.getUserObject();
//	      
	            /* React to the node selection. */
      
            	//System.out.println("Node is a leaf");
            	//Gather up the info from the tfsLogin object
            	
            	//Turns on/off the record button
	            if(searchLinkedList(name,timeList)==null)
	        	{
	        		recordBtn.setSelected(false);
	        		statusLabel.setText("Not Recording");
	        	} else
	        	{
	        		recordBtn.setSelected(true);
	        		statusLabel.setText("Recording Work");
	        	}

				stopWatchPanel.revalidate();
            	//Get Project name
                projectNameField.setText(node.getParent().toString());
                //System.out.println("Parent project: "+node.getParent().toString());
                String projectName = node.getParent().toString();
                //System.out.println(projectName);
                
                //Get project ID
                Project project = TFSProjects.get(node.getParent().toString());
                
                //Create a WorkItemClient
                WorkItemClient wicClient = project.getWorkItemClient();
			       
		        // Define the WIQL query.  
		       String wiqlQuery = "Select ID, Title, Microsoft.VSTS.Scheduling.RemainingWork from WorkItems where (System.TeamProject = '"+project.getName()+"') order by Title";
			       
		       // Run the query and get the results.  
		       WorkItemCollection workItems = wicClient.query(wiqlQuery);
		       //System.out.println("Work Item Size after query: "+workItems.size());
		       //System.out.println("Parent project ID: "+Integer.toString(project.getID()));
//               projectIDField.setText(Integer.toString(project.getID()));
               
//               System.out.println("Task Name: "+node.getUserObject().toString());
//               System.out.println("Work Item Collection Size:"+workItems.size());
               for (int i = 0; i < workItems.size(); i++)
               {	
//            	   System.out.println("There are: "+workItems.size()+" work items.");
            	   if(workItems.getWorkItem(i).getTitle().equals(node.getUserObject().toString()))
            	   {
//            		   System.out.println("Found a task match");
            		   workItemNameField.setText(workItems.getWorkItem(i).getTitle());
            		   projectIDField.setText(Integer.toString(workItems.getWorkItem(i).getID()));
//        			   System.out.println("Work Item ID: "+workItems.getWorkItem(i).getID());
            		   
            		   //FieldCollection fc = workItems.getWorkItem(i).getFields();
        			   
        			   
            		   if(workItems.getWorkItem(i).getFields().getField("Remaining Work").getValue()!=null)
            		   {
	            		   workRemainingField.setText(String.valueOf(workItems.getWorkItem(i).getFields().getField("Remaining Work").getValue()));
//	            		   System.out.println("Remaining Work: "+String.valueOf(workItems.getWorkItem(i).getFields().getField("Remaining Work").getValue()));
            		   } else
            		   {
            			   workRemainingField.setText("");
            		   }
            	   }
        
               }
            } else
            {
            	projectNameField.setText("");
            	projectIDField.setText("");
            	workRemainingField.setText("");
            	workItemNameField.setText("");
            	
            }
            
        }

            
            
        };

    //JToggleButton listener
    class ItemListener implements java.awt.event.ItemListener {
			public void itemStateChanged ( ItemEvent ie ) 
			{
				
				//Get the selected node
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) projectsTree.getLastSelectedPathComponent();
                
				
				if(!getProjectNameField().equals("") && !getWorkRemainingField().equals("") && hasTreeBeenInitialized==true )
				{
					if ( recordBtn.isSelected ( )==true) 
					{
						if(getProjectNameField()!="" )
						{
							statusLabel.setText("Recording work");
							
							if(searchJMenu(getWorkItemNameField(), itemTrackMenu)==false)
							{
								JMenuItem item = new JMenuItem(getWorkItemNameField());
								item.addActionListener(new MenuActionListener());
								itemTrackMenu.add(item);
								//elapsedLbl.setText("");
								date1 = new Date();
							}

//							System.out.println(getProjectNameField());
//							System.out.println(getProjectIDField());
//							System.out.println(getWorkRemainingField());
//							System.out.println(getWorkItemNameField());
							
							//Get the selected node
			                TreePath path = projectsTree.getSelectionPath();
							
			                //Create a time manager
			                TimeManager time = new TimeManager(getProjectNameField(), getProjectIDField(), getWorkRemainingField(), getWorkItemNameField(),date1,path);
							timeList.add(time);
							
							
							
						}
					} else {
						statusLabel.setText("");
						//System.out.println("goodbye");
						
						date2 = new Date();
						difference = date2.getTime() - date1.getTime(); 
						statusLabel.setText(String.valueOf(difference));
						statusLabel.setText(
								String.format("%d hour, %d min, %d sec", 
										TimeUnit.MILLISECONDS.toHours(difference),
									    TimeUnit.MILLISECONDS.toMinutes(difference),
									    TimeUnit.MILLISECONDS.toSeconds(difference) - 
									    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(difference))
									));
						
					
						//System.out.println("Either the node isn't selected or the project name is empty or the remaining work is empty");
						recordBtn.setSelected(false);
					}
				} else {
					recordBtn.setSelected(false);
				}
				stopWatchPanel.revalidate();
			}
			
		};
        
        
    class saveListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(searchLinkedList(workItemNameField.getText(),timeList)!=null)
		{
			Date date = new Date();
			TimeManager time = searchLinkedList(workItemNameField.getText(),timeList);
			time.endDate=date;
			
			statusLabel.setText("");
			//System.out.println("goodbye");
			
			difference = time.endDate.getTime() - date1.getTime(); 
			statusLabel.setText(String.valueOf(difference));
			statusLabel.setText(
					String.format("%d:%d:%d", 
							TimeUnit.MILLISECONDS.toHours(difference),
						    TimeUnit.MILLISECONDS.toMinutes(difference),
						    TimeUnit.MILLISECONDS.toSeconds(difference) - 
						    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(difference))
						));
			
		
			//System.out.println("Either the node isn't selected or the project name is empty or the remaining work is empty");
			recordBtn.setSelected(false);
		
			
			SaveView saveFrame = new SaveView(time, TFSProjects);
			saveFrame.setVisible(true);
		}
	}
        	
        }
        
	
	/**
	 * Getters and setters
	 * @return
	 */
			   
	public DefaultMutableTreeNode getRoot() {
		return root;
	}

	public String getProjectNameField() {
		return projectNameField.getText();
	}

	public String getWorkItemNameField() {
		return workItemNameField.getText();
	}
	
	public String getProjectIDField() {
		return projectIDField.getText();
	}

	public String getWorkRemainingField() {
		return workRemainingField.getText();
	}
	
	
}
