import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.microsoft.tfs.core.httpclient.Credentials;
import com.microsoft.tfs.core.httpclient.UsernamePasswordCredentials;

public class LoginView extends JFrame implements ActionListener {

	
	private LoginTFS login;
	private JPanel loginPanel;
	private JFrame loginFrame;
	private JLabel userLabel;
	private JLabel passwordLabel;
	private JLabel tfsURILabel;
	private JTextField userText;
	private JTextField tfsURIText;
	private JButton loginButton;
	private JPasswordField passwordText;
	
	public LoginView() {
		super("TimeKeeper");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel loginPanel = new JPanel();
		add(loginPanel);


		userLabel = new JLabel("User:");
		userLabel.setBounds(10, 10, 80, 25);
		loginPanel.add(userLabel);

		userText = new JTextField(21);
		userText.setBounds(100, 10, 160, 25);
		loginPanel.add(userText);

		passwordLabel = new JLabel("Password:");
		passwordLabel.setBounds(10, 40, 80, 25);
		loginPanel.add(passwordLabel);

		passwordText = new JPasswordField(18);
		passwordText.setBounds(100, 40, 160, 25);
		loginPanel.add(passwordText);
		
		tfsURILabel = new JLabel("TFS URI:");
		tfsURILabel.setBounds(10, 10, 80, 25);
		loginPanel.add(tfsURILabel);

		tfsURIText = new JTextField(20);
		tfsURIText.setBounds(100, 10, 160, 25);
		loginPanel.add(tfsURIText);
		
		loginButton = new JButton("Login");
		loginButton.setBounds(10, 80, 80, 25);
		loginPanel.add(loginButton);
		loginButton.addActionListener(this);
		pack();
	}
	//Execute when login button is pressed
	@Override
	public void actionPerformed(ActionEvent e)
    {
        //Execute when button is pressed
        char[] validatePArray = passwordText.getPassword();
        String validateP;
        StringBuilder validatePBuilder = new StringBuilder(20);
        
        for(int i = 0; i < validatePArray.length; i++)
        {
        	validatePBuilder.append(validatePArray[i]);
        }
        
        validateP = validatePBuilder.toString();
        
        String validateU = userText.getText();
        String tfsURI = tfsURIText.getText();
   
        URI uri = null;

		try {
			uri = new URI(tfsURI);
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

//		
//		System.out.println(uri.toString());
//        System.out.println(validateU);
//        System.out.println(validateP);

        Credentials credentials = new UsernamePasswordCredentials(validateU, validateP);
        LoginTFS tfsLogin = null;
		tfsLogin = new LoginTFS(uri, credentials);
        
        dispose();
        new MainView(tfsLogin);
    }
	
	public static void main(String[] args) {
		
		System.setProperty("com.microsoft.tfs.jni.native.base-directory", "C:/Users/Development/Downloads/TFS-SDK-11.0.0.1306/TFS-SDK-11.0.0/redist/native");
		
		SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run()
            {
                new LoginView().setVisible(true);
            }

        });
	}

}


  

 
