package pippin;

import java.util.Map;
import java.util.TreeMap;
import java.util.Observable;

public class MachineModel extends Observable {
	public final Map<Integer, Instruction> INSTRUCTIONS = new TreeMap<>();
	private Registers cpu = new Registers();
	private Memory memory = new Memory();
	private boolean withGUI = false;
	private Code code = new Code();
	private boolean running = false;

	public MachineModel(){
		this(false);
	}

	public MachineModel(boolean b) {
		this.withGUI = b;

		//INSTRUCTIONS entry for "NOP"
		INSTRUCTIONS.put(0x0, arg -> {
			cpu.programCounter++;
		});

		//INSTRUCTIONS entry for "LODI"
		INSTRUCTIONS.put(0x1, arg -> {
			cpu.accumulator = arg;
			cpu.programCounter++;
		});

		//INSTRUCTIONS entry for "LOD"
		INSTRUCTIONS.put(0x2, arg -> {
			INSTRUCTIONS.get(0x1).execute(memory.getData(arg));
		});

		//INSTRUCTIONS entry for "LODN"
		INSTRUCTIONS.put(0x3, arg -> {
			INSTRUCTIONS.get(0x2).execute(memory.getData(arg));
		});

		//INSTRUCTIONS entry for "STO"
		INSTRUCTIONS.put(0x4, arg -> {
			memory.setData(arg, cpu.accumulator);
			cpu.programCounter++;
		});

		//INSTRUCTIONS entry for "STON"
		INSTRUCTIONS.put(0x5, arg -> {
			INSTRUCTIONS.get(0x4).execute(memory.getData(arg));
		});

		//INSTRUCTIONS entry for "JMPI"
		INSTRUCTIONS.put(0x6, arg -> {
			cpu.programCounter = arg;
		});

		//INSTRUCTIONS entry for "JUMP"
		INSTRUCTIONS.put(0x7, arg -> {
			INSTRUCTIONS.get(0x6).execute(memory.getData(arg));
		});

		//INSTRUCTIONS entry for "JMZI"
		INSTRUCTIONS.put(0x8, arg -> {
			if(cpu.accumulator == 0){
				cpu.programCounter = arg;
			}else{
				cpu.programCounter++;
			}
		});

		//INSTRUCTIONS entry for "JMPZ"
		INSTRUCTIONS.put(0x9, arg -> {
			INSTRUCTIONS.get(0x8).execute(memory.getData(arg));
		});

		//INSTRUCTIONS entry for "ADDI"
		INSTRUCTIONS.put(0xA, arg -> {
			cpu.accumulator += arg;
			cpu.programCounter++;
		});

		//INSTRUCTIONS entry for "ADD"
		INSTRUCTIONS.put(0xB, arg -> {
			INSTRUCTIONS.get(0xA).execute(memory.getData(arg));
		});

		//INSTRUCTIONS entry for "ADDN"
		INSTRUCTIONS.put(0xC, arg -> {
			INSTRUCTIONS.get(0xB).execute(memory.getData(arg));
		});

		//INSTRUCTIONS entry for "SUBI"
		INSTRUCTIONS.put(0xD, arg -> {
			cpu.accumulator -= arg;
			cpu.programCounter++;
		});

		//INSTRUCTIONS entry for "SUB"
		INSTRUCTIONS.put(0xE, arg -> {
			INSTRUCTIONS.get(0xD).execute(memory.getData(arg));
		});

		//INSTRUCTIONS entry for "SUBM"
		INSTRUCTIONS.put(0xF, arg -> {
			INSTRUCTIONS.get(0xE).execute(memory.getData(arg));
		});

		//INSTRUCTIONS entry for "MULI"
		INSTRUCTIONS.put(0x10, arg -> {
			cpu.accumulator *= arg;
			cpu.programCounter++;
		});

		//INSTRUCTIONS entry for "MUL"
		INSTRUCTIONS.put(0x11, arg -> {
			INSTRUCTIONS.get(0x10).execute(memory.getData(arg));
		});

		//INSTRUCTIONS entry for "MULN"
		INSTRUCTIONS.put(0x12, arg -> {
			INSTRUCTIONS.get(0x11).execute(memory.getData(arg));
		});

		//INSTRUCTIONS entry for "DIVI"
		INSTRUCTIONS.put(0x13, arg -> {
			if(arg == 0){
				DivideByZeroException d = new DivideByZeroException();
				throw d; 
			}else{
				cpu.accumulator /= arg;
				cpu.programCounter++;
			}});

		//INSTRUCTIONS entry for "DIV"
		INSTRUCTIONS.put(0x14, arg -> {
			INSTRUCTIONS.get(0x13).execute(memory.getData(arg));
		});

		//INSTRUCTIONS entry for "DIVN"
		INSTRUCTIONS.put(0x15, arg -> {
			INSTRUCTIONS.get(0x14).execute(memory.getData(arg));
		});

		//INSTUCTIONS entry for "ANDI"
		INSTRUCTIONS.put(0x16, arg -> {
			if(arg !=0 && cpu.accumulator !=0){
				cpu.accumulator = 1;
			}else{
				cpu.accumulator = 0;
			}
			cpu.programCounter++;
		});

		//INSTRUCTIONS entry for "AND"
		INSTRUCTIONS.put(0x17, arg -> {
			INSTRUCTIONS.get(0x16).execute(memory.getData(arg));
		});

		//INSUTRCTIONS entry for "NOT"
		INSTRUCTIONS.put(0x18, arg -> {
			if(cpu.accumulator == 0){
				cpu.accumulator = 1;
			}else{
				cpu.accumulator = 0;
			}
			cpu.programCounter++;
		});

		//INSTRUCTIONS entry for "CMPL"
		INSTRUCTIONS.put(0x19, arg -> {
			if(memory.getData(arg) < 0){
				cpu.accumulator =1;
			}else{
				cpu.accumulator = 0;
			}
			cpu.programCounter ++;
		});

		//INSTRUCTIONS entry for "CMPZ"
		INSTRUCTIONS.put(0x1A, arg -> {
			if(memory.getData(arg) == 0){
				cpu.accumulator = 1;
			}else{
				cpu.accumulator = 0;
			}	
			cpu.programCounter++;
		});

		//INSTRUCTIONS entry for "HALT"
		INSTRUCTIONS.put(0x1F, arg -> {
			halt();
		});

		//INSTRUCTION_MAP entry for "COPY"
		INSTRUCTIONS.put(0x1D,(arg) -> {
			if(arg <= 0 || arg+1 <= 0 || arg+2 <= 0 ||
			   arg >= Memory.DATA_SIZE-1 || arg+1 >= Memory.DATA_SIZE-1 || arg+2 >= Memory.DATA_SIZE-1){
				throw new IllegalArgumentException ("Instruction is out of range");
			}else{
				copy(arg);
			}
			cpu.programCounter++;
		});

		//INSTRUCTION entry for "CPYN"
		INSTRUCTIONS.put(0x1E, (arg) -> {
			if(arg <= 0 || arg+1 <= 0 || arg+2 <= 0 ||
				arg >= Memory.DATA_SIZE-1 || arg+1 >= Memory.DATA_SIZE-1 || arg+2 >= Memory.DATA_SIZE-1){
				throw new IllegalArgumentException ("Instruction is out of range");
			}else{
				INSTRUCTIONS.get(0x1D).execute(memory.getData(arg));
			}
		});
	}

	public class Registers{
		private int accumulator;
		private int programCounter;
	}
	public int getData(int index) {
		return memory.getData(index);
	}
	public void setData(int index, int value) {
		memory.setData(index, value);
	}
	public Instruction get(Object key) {
		return INSTRUCTIONS.get(key);
	}
	int[] getData() {
		return memory.getData();
	}
	public int getProgramCounter() {
		return cpu.programCounter;
	}
	public int getAccumulator() {
		return cpu.accumulator;
	}
	void setAccumulator(int i) {
		cpu.accumulator = i;
	}
	void setProgramCounter(int i) {
		cpu.programCounter = i;
	}

	//add the following methods
	public int getChangedIndex() {
		return memory.getChangedIndex();
	}
	public void clearMemory() {
		memory.clear();
	}

	//package private getter method for code
	Code getCode(){
		return code;
	}

	public void setCode(int op, int arg){
		code.setCode(op, arg);
	}

	void halt() {
		if(withGUI) {
			running = false;
		} else {
			System.exit(0);
		}
	}

	public void setRunning(boolean running){
		this.running = running;
	}

	public boolean isRunning(){
		return running;
	}

	public void step(){
		try{
			int pc = cpu.programCounter;
			int opcode = code.getOp(pc);
			int arg = code.getArg(pc);
			get(opcode).execute(arg);
		}catch(Exception e){
			halt( ); throw e;
		}
	}

	public void clear(){
		Memory memory = new Memory();
		memory.clear();
		Code code = new Code();
		code.clear();
		cpu.accumulator = 0;
		cpu.programCounter = 0;
	}

	/**
	 * copy takes a part of memory starting at source (arg) and of length arg+2, and copies it
	 * over to memory location target (arg+1); If the source is less than the target, the copying starts at 
	 * target+length-1 and continues down to target. If, instead, the source is greater than the target, we 
	 * start the copy at source and work down its length.
	 * @param arg location in memory that contains the first elements to be copied. Arg+1 is used as a reference point
	 * for the target and Arg+2 is used as a reference point for the length of the elements to be copied. 
	 */

	public void copy(int arg) {
		int source = memory.getData(arg);
		int target = memory.getData(arg+1);
		int length = memory.getData(arg+2);
		//if it is in between the ranges that's an error
		if((arg > source && arg < source+length) || 
				(arg+1 > source && arg+1 < source+ length) ||
				(arg+2 > source && arg+2 < source+ length) || (arg > target && arg < target+ length) ||
				(arg+1 > target && arg+1 < target+ length) || (arg+2 > target && arg+2 < target+ length)){
			throw new IllegalArgumentException ("Instruction would corrupt arg");
		}else{
			if(source < target){
				int inc = 1;
				for(int i = target+length-1; i>=target; i--){
					int num = memory.getData(source+length-inc);
					memory.setData(i, num);
					inc++;
				}
			}else{
				int dec = 0;
				for(int j = 0; j < length; j++){
					int num1 = memory.getData(source + dec);
					memory.setData(target + dec, num1);
					dec++;
				}
			}
		}
	}
}