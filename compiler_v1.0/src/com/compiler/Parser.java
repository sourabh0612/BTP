package com.compiler;

//Add space error handler
public class Parser {

	public static void term(){
		factor();
		while(Utils.look=='*' || Utils.look=='/'){
			Utils.emitLn("PUSH EAX");
			switch(Utils.look){
				case '*': multiply();break;
				case '/': divide();break;
				default: Utils.expected("Mulop");break;
			}
		}
	}
	
	public static void expression(){
		term();
		while(Utils.look=='+' || Utils.look=='-'){
			Utils.emitLn("PUSH EAX");
			switch(Utils.look){
				case '+': add();break;
				case '-': subtract();break;
				default: Utils.expected("Addop");break;
			}
		}
	}
	
	public static void factor(){
		if(Utils.look=='('){
			Utils.matchChar('(');
			expression();
			Utils.matchChar(')');
		}
		else if(Utils.isAlpha(Utils.look))
				ident();
			Utils.emitLn("MOV EAX,"+Utils.getNum());
	}
	
	public static void ident(){
		String name;
		name=Utils.getName();
		if(Utils.look=='('){
			Utils.matchChar(')');
			Utils.matchChar(')');
			Utils.emitLn("CALL "+ name);
		}
		else
			Utils.emitLn("MOV EAX,["+Utils.getName()+"]");
	
	}
	
	public static void assignment(){
		String name;
		name=Utils.getName();
		Utils.matchChar('=');
		expression();
		//check this
		//Utils.emitLn("LEA EBX,["+name+"]");
		Utils.emitLn("MOVE EAX,["+name+"]");
	}
	
	public static void other(){
		Utils.emitLn(Utils.getName());
	}
	
	public static void doProgram(){
		block("");
		if(Utils.look!='e')
			Utils.expected("END");
		Utils.emitLn("END");
	}
	
	public static void block(String label){
		while(Utils.look!='e' && Utils.look!='l' && Utils.look!='u'){
			switch(Utils.look){
			case 'i': doIf(label);break;
			case 'w':doWhile();break;
			case 'p':doLoop();break;
			case 'r':doRepeat();break;
			case 'f':doFor();break;
			case 'd':doDo();break;
			case 'b':doBreak(label);break;
			default:other();
			}
			//other();			
		}
	}
	
	public static void condition(){
		Utils.emitLn("<Condition>");
	}
	
	
	/*
	 * Control Constructs
	 */
	/*
	 * Usage: b in any block
	 */
	public static void doBreak(String label){
		Utils.matchChar('b');
		if(!label.equals(""))
			Utils.emitLn("JMP "+label);
		else
			Utils.abort("No loop to break from");
	}
	
	/*
	 * BNF: IF <condition> <block> [ ELSE <block>] ENDIF
	 * Usage: i(if) c(block) l(else) g(block) e(end)
	 */
	public static void doIf(String label){
		String l1,l2;
		Utils.matchChar('i');
		condition();
		l1=Utils.newLabel();
		l2=l1;
		Utils.emitLn("JZ "+l1);
		block(label);
		if(Utils.look=='l'){
			Utils.matchChar('l');
			l2=Utils.newLabel();
			Utils.emitLn("JMP "+l2);
			Utils.postLabel(l1);
			block(label);
		}
		Utils.matchChar('e');
		Utils.postLabel(l2);
	}
	
	/*
	 * BNF: WHILE <condition> <block> ENDWHILE
	 * Usage: w(while) c(block) e(end)
	 */
	public static void doWhile(){
		String l1,l2;
		Utils.matchChar('w');
		l1=Utils.newLabel();
		l2=Utils.newLabel();
		Utils.postLabel(l1);
		condition();
		Utils.emitLn("JZ "+l2);
		block(l2);
		Utils.matchChar('e');
		Utils.emitLn("JMP "+l1);
		Utils.postLabel(l2);
	}
	
	/*
	 * BNF: LOOP <block> ENDLOOP
	 * Usage: p(loop) c(block) e(end)
	 */
	public static void doLoop(){
		String l1,l2;
		Utils.matchChar('p');
		l1=Utils.newLabel();
		l2=Utils.newLabel();
		Utils.postLabel(l1);
		block(l2);
		Utils.matchChar('e');
		Utils.emitLn("JMP "+l1);
		Utils.postLabel(l2);
	}
	
	/*
	 * BNF: REPEAT <block> UNTIL <condition> 
	 * Usage: r(repeat) c(block) u(until)
	 */
	public static void doRepeat(){
		String l1,l2;
		Utils.matchChar('r');
		l1=Utils.newLabel();
		l2=Utils.newLabel();
		Utils.postLabel(l1);
		block(l2);
		Utils.matchChar('u');
		condition();
		Utils.emitLn("JNZ "+l1);
		Utils.postLabel(l2);
	}
	
	/*
	 * BNF: FOR <ident> = <expr1> <expr2> <block> ENDFOR
	 * Usage: f(for) c(ident) = g(block) e(end)
	 */
	public static void doFor(){
		String l1,l2;
		String name;
		Utils.matchChar('f');
		l1=Utils.newLabel();
		l2=Utils.newLabel();
		name=Utils.getName();
		Utils.matchChar('=');
		boolExpression();
		Utils.emitLn("DEC EAX");
		Utils.emitLn("MOV ["+name+"],EAX");
		boolExpression();
		Utils.emitLn("PUSH EAX");
		Utils.postLabel(l1);
		Utils.emitLn("MOV ["+name+"]+EAX");
		Utils.emitLn("INC EAX");
		Utils.emitLn("MOV ["+name+"], EAX");
		Utils.emitLn("CMP EAX,[ESP]");
		Utils.emitLn("JGE "+l2);
		block(l2);
		Utils.matchChar('e');
		Utils.emitLn("JMP "+l1);
		Utils.postLabel(l2);
		Utils.emitLn("INC ESP");
		Utils.emitLn("INC ESP");
	}
	
	/*
	 * BNF: DO <expr> <block> ENDDO
	 * Usage: d(do) c(block) 
	 */
	public static void doDo(){
		String l1,l2;
		Utils.matchChar('d');
		l1=Utils.newLabel();
		l2=Utils.newLabel();
		boolExpression();
		Utils.emitLn("DEC EAX");
		Utils.postLabel(l1);
		Utils.emitLn("PUSH EAX");
		block(l2);
		Utils.emitLn("POP EAX");
		Utils.emitLn("DEC EAX");
		Utils.emitLn("JNZ "+l1);
		Utils.emitLn("DEC ESP");
		Utils.emitLn("DEC ESP");
		Utils.postLabel(l2);
		Utils.emitLn("INC ESP");
		Utils.emitLn("INC ESP");
	}
	
	
	/*
	 * New function for loop variants
	 */
	public static void boolExpression(){
		Utils.emitLn("Dummy expression");
	}
	
	
	/*
	 * addOps and mulOps
	 */
	public static void add(){
		Utils.matchChar('+');
		term();
		Utils.emitLn("POP EBX");
		Utils.emitLn("ADD EAX,EBX");
	}
	
	public static void subtract(){
		Utils.matchChar('-');
		term();
		Utils.emitLn("POP EBX");
		Utils.emitLn("SUB EAX,EBX");
		Utils.emitLn("NEG EAX");
	}
	
	public static void multiply(){
		Utils.matchChar('*');
		factor();
		Utils.emitLn("POP EBX");
		Utils.emitLn("IMUL EAX,EBX");
	}
	
	public static void divide(){
		Utils.matchChar('/');
		factor();
		Utils.emitLn("POP EBX");
		Utils.emitLn("IDIV EBX");
	}
	
}