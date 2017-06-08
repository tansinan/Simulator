package logic.components;

import logic.RenamedValue;
import logic.ReservationStation;
import logic.TomasuloCircuit;
import logic.components.InstructionMemory.ExecStatus;
import logic.components.InstructionMemory.Instruction;
import logic.components.InstructionMemory.LoadStoreInstruction;

public class StoreBuffer extends ReservationStation { 

	public class EntryData extends ReservationStation.Entry {
		public EntryData(Instruction instruction, ReservationStation rs, int index, int baseAddress, int offset) {
			super(instruction, rs, index);
			this.baseAddress = baseAddress;
			this.offset = offset;
		}
		public int baseAddress; //Note: We assume regular registers will never be change...
		public int offset;
		public RenamedValue value;
		@Override
		public boolean readyToExecute() {
			// TODO Auto-generated method stub
			return true;
		}
	}
	
	public EntryData[] entries;
	public StoreBuffer(TomasuloCircuit circuit, int size)
	{
		super(circuit, size);
		entries = new EntryData[size];
	}
	
	@Override
	public void onClockTick() {
		for(int i = 0; i < entries.length; i++)
		{
			if(entries[i] == null) {
				continue;
			}
			if(entries[i].instruction.execStatus == ExecStatus.RUNNING)
			{
				if(entries[i].value.isAvailable == true)
				{
					circuit.dataMemory.setData(entries[i].baseAddress + entries[i].offset, entries[i].value.value);
					entries[i] = null;
				}
			}
			else if(entries[i].instruction.execStatus == ExecStatus.QUEUED &&
					entries[i].readyToExecute())
			{
				entries[i].instruction.execStatus = ExecStatus.RUNNING;
			}
		}
		
	}
	
	@Override
	public void onBroadcast(ReservationStation.Entry entry, float data) {
		for(int i = 0; i < entries.length; i++)
		{
			if(entries[i] == null) {
				continue;
			}
			if(entries[i].value.rsEntry == entry)
			{
				entries[i].value.isAvailable = true;
				entries[i].value.value = data;
			}
		}
		
	}

	@Override
	public String getName() {
		return "Store";
	}

	public boolean addInstruction(LoadStoreInstruction inst) {
		for(int i = 0; i < size; i++)
		{
			if(entries[i] == null) {
				EntryData entryData = new EntryData(
					inst, this, i, 
					circuit.regularRegisterFile.getData(inst.baseRegister),
					inst.offset);
				entryData.value = new RenamedValue(circuit.fpRegisterFile, inst.dataRegister);
				entries[i] = entryData;
				return true;
			}
		}
		return false;
	}
}
