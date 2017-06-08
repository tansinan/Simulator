package gui;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import logic.components.DataMemory;
import logic.components.InstructionMemory;
import logic.components.Operation;
import logic.components.InstructionMemory.ExecStatus;
import logic.components.InstructionMemory.FPUInstruction;
import logic.components.InstructionMemory.Instruction;
import logic.components.InstructionMemory.LoadStoreInstruction;

public class RuntimePanel extends JPanel{
	private JLabel label;
	private JTable table;
	private DefaultTableModel tableModel;
	private JScrollPane scroller;
	protected InstructionMemory instructionMemory;
	
	RuntimePanel(){
		label = new JLabel("运行状态", SwingConstants.CENTER);
		label.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
		
		String[] colName = {"指令", "运行状态", "写回结果"};
		tableModel = new DefaultTableModel(null, colName);
		table = new JTable(tableModel);
		table.setRowHeight(25);
		scroller = new JScrollPane(table);
        scroller.setPreferredSize(new Dimension(300, 200));
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(label);
        add(scroller);
	}

	public void bindInstructionMemory(InstructionMemory instructionMemory) {
		this.instructionMemory = instructionMemory;
		updateFromLogic();
	}
	
	public void updateFromLogic() {
		int memorySize = instructionMemory.getSize();
		if(memorySize != tableModel.getRowCount()) {
			tableModel.setRowCount(memorySize);
		}
		for(int i = 0; i < memorySize; i++)
		{
			Instruction instruction = instructionMemory.getInstruction(i);
			if(instruction == null)
			{
				tableModel.setValueAt("NOP", i, 1);
				for(int j = 0; j < 3; j++) {
					tableModel.setValueAt("", i, j);
				}
				continue;
			}
			tableModel.setValueAt("" + Operation.OperationAbbr(instruction.operation), i, 0);
			String s = "";
			switch(instruction.execStatus)
			{
			case DONE:
				s = "完成";
				break;
			case QUEUED:
				s = "进入保留站或队列";
				break;
			case RUNNING:
				s = "正在执行";
				break;
			case WAITING:
				s = "正在等待";
				break;
			default:
				break;	
			}
			tableModel.setValueAt(s, i, 1);
			if(instruction.execStatus == ExecStatus.DONE) {
				tableModel.setValueAt("Yes", i, 2);
			}
			else {
				tableModel.setValueAt("No", i, 2);
			}
			//tableModel.setValueAt("F" + fpInst.source1, i, 3);
		}
	}
	
	public void writeToLogic() {
		
	}
	
	void addRuntime(String instruction) {
		tableModel.addRow(new String[]{instruction, "No", ""});
	}
	
	void modifyRuntime(int num, String instruction, boolean status, String writeBack) {
		tableModel.setValueAt(instruction, num, 0);
		if(status == true) {
			tableModel.setValueAt("Yes", num, 1);
		}
		else {
			tableModel.setValueAt("No", num, 1);
		}
		tableModel.setValueAt(writeBack, num, 2);
	}
}
