import java.util.Date;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
/*
 * Simple class to record the work done on an object.
 * Records the name of the parent project, its work item ID,
 * the name of the work item, the work remaining, the starting date,
 * and the path on the JTree where the work item can be found.
 */
public class TimeManager 
{
	String projectName, workItemID, workRemaining, workItemName;
	Date startDate, endDate;
	DefaultMutableTreeNode node;
	TreePath path;
	
	public TimeManager(String name, String itemID, String work, String itemName, Date sDate, TreePath tPath)
	{
		projectName = name;
		workItemID=itemID;
		workRemaining=work;
		workItemName = itemName;
		startDate = sDate;
		path = tPath;
	}
}
