import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.ListSelectionModel;

import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import javax.swing.ScrollPaneConstants;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import java.awt.Color;
import java.awt.Component;


/**
 * Manages a list of PaymentPeriodPanels. Each PaymentPeriodPanel is represented vertically in
 * order.
 * 
 * Since no payments or interest is applied to PaymentPeriod 0, only the PaymentPeriodTable is displayed. 
 * 
 * 
 * @author Phil Edie
 *
 */
public class ResultsPanel extends JPanel {

	private GUI gui;
	List<PaymentPeriodPanel> childPanels = new ArrayList<PaymentPeriodPanel>();


	public ResultsPanel(GUI gui) {

		this.gui = gui;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JLabel lblNewLabel = new JLabel("Please enter account information within the \"Accounts\" tab and click \"Confirm\".");
		lblNewLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblNewLabel);
	}

	
	/**
	 * Creates a list of PaymentPeriodPanels from the information stored in the account manager.
	 * Displays the list of PaymentPeriodPanels in order. 
	 * 
	 * The first PaymentPeriodPanel only contains a PaymentPeriodTable. 
	 */
	public void update() {
		assert gui.accountManager.getHistory().size() > 0;
		this.removeAll();
		Stack<List<Account>> history = gui.accountManager.getHistory();

		for (int i = 0; i < history.size(); i++) {
			List<Account> accounts = history.get(i);
			PaymentPeriodPanel panel = new PaymentPeriodPanel();
			if(i == 0) {
				panel.outerPanelLabel.setText("Accounts Summary");
			} else {
				panel.outerPanelLabel.setText("Payment Period " + i);
			}
			this.add(panel);
			
		
			DefaultTableModel model = (DefaultTableModel) panel.paymentPeriodTable.getModel();
			
			
			//For the payment period table.
			for (Account account : accounts) {
				//Create a row for each account.
				model.addRow(new Object[] {
						account.getAccountName(), account.getClass().getSimpleName(),
						Utilities.convertToDollarFormat(account.getBalance()),
						Utilities.convertToPercentageFormat((account.getInterestRate() - 1) * 100),
						Utilities.convertToDollarFormat(account.getPaymentForPeriod()),
						Utilities.convertToDollarFormat(account.getInterestForPeriod()) });
			}
			
			
			//Change the scroll pane to be only as large as needed.
			Dimension d = panel.paymentPeriodTable.getPreferredSize();
			panel.paymentPeriodScrollPane.setPreferredSize(
			    new Dimension(d.width,panel.paymentPeriodTable.getRowHeight()*(accounts.size() + 2)));
			
			//For the payment period summarytable. We don't need as much information since no interest or payments are made.
			
			if(i == 0) {
				panel.outerPanel.remove(panel.summaryLabel);
				panel.outerPanel.remove(panel.innerPanel2);
				
				panel.paymentPeriodTable.removeColumn(panel.paymentPeriodTable.getColumnModel().getColumn(5));
				panel.paymentPeriodTable.removeColumn(panel.paymentPeriodTable.getColumnModel().getColumn(4));
				
				
				panel.revalidate();
				continue;
			}
			
			//There should only be a change in net worth starting from the first payment period. 
			String changeInNetWorth = "$0.00";
			if(i > 0) {
				changeInNetWorth = Utilities.getChangeInNetWorthAsString(history.get(i-1), accounts);
			}
			
			model = (DefaultTableModel) panel.paymentPeriodSummaryTable.getModel();
			model.addRow(new Object[] {
					Utilities.getPaidOffLoanNames(accounts),
					Utilities.getNetWorthAsString(accounts),
					changeInNetWorth,
					Utilities.getTotalInterestAsString(accounts)	
			});
			
			//Change the scroll pane to be only as large as needed.
			d = panel.paymentPeriodSummaryTable.getPreferredSize();
			panel.paymentPeriodSummaryScrollPane.setPreferredSize(
			    new Dimension(d.width,panel.paymentPeriodSummaryTable.getRowHeight() + 1));
		}

	}
}
