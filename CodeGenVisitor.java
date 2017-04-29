package cop5556sp17;

import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.TraceClassVisitor;
//import org.objectweb.asm.Type;

import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.Token;
import cop5556sp17.TypeCheckVisitor.TypeCheckException;
import cop5556sp17.AST.ASTVisitor;
import cop5556sp17.AST.AssignmentStatement;
import cop5556sp17.AST.BinaryChain;
import cop5556sp17.AST.BinaryExpression;
import cop5556sp17.AST.Block;
import cop5556sp17.AST.BooleanLitExpression;
import cop5556sp17.AST.Chain;
import cop5556sp17.AST.ChainElem;
import cop5556sp17.AST.ConstantExpression;
import cop5556sp17.AST.Dec;
import cop5556sp17.AST.Expression;
import cop5556sp17.AST.FilterOpChain;
import cop5556sp17.AST.FrameOpChain;
import cop5556sp17.AST.IdentChain;
import cop5556sp17.AST.IdentExpression;
import cop5556sp17.AST.IdentLValue;
import cop5556sp17.AST.IfStatement;
import cop5556sp17.AST.ImageOpChain;
import cop5556sp17.AST.IntLitExpression;
import cop5556sp17.AST.ParamDec;
import cop5556sp17.AST.Program;
import cop5556sp17.AST.SleepStatement;
import cop5556sp17.AST.Statement;
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.Type;
import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.AST.WhileStatement;

import static cop5556sp17.AST.Type.TypeName.BOOLEAN;
import static cop5556sp17.AST.Type.TypeName.FILE;
import static cop5556sp17.AST.Type.TypeName.FRAME;
import static cop5556sp17.AST.Type.TypeName.IMAGE;
import static cop5556sp17.AST.Type.TypeName.INTEGER;
import static cop5556sp17.AST.Type.TypeName.NONE;
import static cop5556sp17.AST.Type.TypeName.URL;
import static cop5556sp17.Scanner.Kind.*;

public class CodeGenVisitor implements ASTVisitor, Opcodes {
	
	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 */
	public CodeGenVisitor(boolean DEVEL, boolean GRADE, String sourceFileName) {
		super();
		this.DEVEL = DEVEL;
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;
		paramCount = 0;
		runDecMaxSlot = 0;
		runDecs = new ArrayList<Dec>();
	}

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;

	MethodVisitor mv; // visitor of method currently under construction

	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;
	
	int paramCount;
	int runDecMaxSlot;
	ArrayList<Dec> runDecs;

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		//cw = new ClassWriter(0);
		className = program.getName();
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object",
				new String[] { "java/lang/Runnable" });
		cw.visitSource(sourceFileName, null);

		// generate constructor code
		// get a MethodVisitor
		mv = cw.visitMethod(ACC_PUBLIC, "<init>", "([Ljava/lang/String;)V", null,
				null);
		mv.visitCode();
		// Create label at start of code
		Label constructorStart = new Label();
		mv.visitLabel(constructorStart);
		// this is for convenience during development--you can see that the code
		// is doing something.
		//CodeGenUtils.genPrint(DEVEL, mv, "\nentering <init>");
		// generate code to call superclass constructor
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		// visit parameter decs to add each as field to the class
		// pass in mv so decs can add their initialization code to the
		// constructor.
		ArrayList<ParamDec> params = program.getParams();
		for (ParamDec dec : params)
			dec.visit(this, mv);
		mv.visitInsn(RETURN);
		// create label at end of code
		Label constructorEnd = new Label();
		mv.visitLabel(constructorEnd);
		// finish up by visiting local vars of constructor
		// the fourth and fifth arguments are the region of code where the local
		// variable is defined as represented by the labels we inserted.
		mv.visitLocalVariable("this", classDesc, null, constructorStart, constructorEnd, 0);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, constructorStart, constructorEnd, 1);
		// indicates the max stack size for the method.
		// because we used the COMPUTE_FRAMES parameter in the classwriter
		// constructor, asm will do this for us. The parameters to visitMaxs
		//don't matter, but the method must be called.
		mv.visitMaxs(1, 1);
		// finish up code generation for this method.
		mv.visitEnd();
		// end of constructor

		// create main method which does the following
		// 1. instantiate an instance of the class being generated, passing the
		// String[] with command line arguments
		// 2. invoke the run method.
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null,
				null);
		mv.visitCode();
		Label mainStart = new Label();
		mv.visitLabel(mainStart);
		// this is for convenience during development--you can see that the code
		// is doing something.
		//CodeGenUtils.genPrint(DEVEL, mv, "\nentering main");
		mv.visitTypeInsn(NEW, className);
		mv.visitInsn(DUP);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, className, "<init>", "([Ljava/lang/String;)V", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, className, "run", "()V", false);
		mv.visitInsn(RETURN);
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart, mainEnd, 0);
		mv.visitLocalVariable("instance", classDesc, null, mainStart, mainEnd, 1);
		mv.visitMaxs(0, 0);
		mv.visitEnd();

		// create run method
		mv = cw.visitMethod(ACC_PUBLIC, "run", "()V", null, null);
		mv.visitCode();
		Label startRun = new Label();
		mv.visitLabel(startRun);
		//CodeGenUtils.genPrint(DEVEL, mv, "\nentering run");
		program.getB().visit(this, null);
		mv.visitInsn(RETURN);
		Label endRun = new Label();
		mv.visitLabel(endRun);
		mv.visitLocalVariable("this", classDesc, null, startRun, endRun, 0);
//TODO  visit the local variables
		for(Dec dec : runDecs)
			mv.visitLocalVariable(dec.getIdent().getText(), Type.getTypeName(dec.getType()).getJVMTypeDesc(), null, dec.getStart(), dec.getEnd(), dec.getSlotID());
		
		mv.visitMaxs(1, 1);
		mv.visitEnd(); // end of run method
		
		cw.visitEnd();//end of class
		
		//generate classfile and return it
		return cw.toByteArray();
	}

	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
		assignStatement.getE().visit(this, arg);
		//CodeGenUtils.genPrint(DEVEL, mv, "\nassignment: " + assignStatement.var.getText() + "=");
		//CodeGenUtils.genPrint(DEVEL, mv, "", assignStatement.getE().getTypeName());
		CodeGenUtils.genPrintTOS(GRADE, mv, assignStatement.getE().getTypeName());
		
		if(assignStatement.getVar().getTypeName() == IMAGE)
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "copyImage", PLPRuntimeImageOps.copyImageSig, false);
		
		assignStatement.getVar().visit(this, arg);
		
		return null;
	}

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {
		binaryChain.getE0().visit(this, 0);
		
		if(binaryChain.getE0().getTypeName() == URL)
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "readFromURL", PLPRuntimeImageIO.readFromURLSig, false);
		else if(binaryChain.getE0().getTypeName() == FILE)
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "readFromFile", PLPRuntimeImageIO.readFromFileDesc, false);
		
		if(binaryChain.getE1() instanceof FilterOpChain) {
			if(binaryChain.getArrow().isKind(BARARROW) && binaryChain.getE1().getFirstToken().isKind(OP_GRAY)) {
				mv.visitInsn(DUP);
				//mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "copyImage", PLPRuntimeImageOps.copyImageSig, false);
				//mv.visitInsn(SWAP);
			}
			else
				mv.visitInsn(ACONST_NULL);
		}
		
		binaryChain.getE1().visit(this, 1);
		
		return null;
	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
      //TODO  Implement this
		binaryExpression.getE0().visit(this, null);
		binaryExpression.getE1().visit(this, null);
		
		Label pushTrue = new Label();
		Label after = new Label();
		
		if((binaryExpression.getE0().getTypeName() == Type.TypeName.INTEGER && binaryExpression.getE1().getTypeName() == Type.TypeName.INTEGER) || 
				(binaryExpression.getE0().getTypeName() == Type.TypeName.BOOLEAN && binaryExpression.getE1().getTypeName() == Type.TypeName.BOOLEAN)) {
			switch(binaryExpression.getOp().kind) {
			case PLUS:
				mv.visitInsn(IADD);
				mv.visitJumpInsn(GOTO, after);
				break;
			case MINUS:
				mv.visitInsn(ISUB);
				mv.visitJumpInsn(GOTO, after);
				break;
			case TIMES:
				mv.visitInsn(IMUL);
				mv.visitJumpInsn(GOTO, after);
				break;
			case DIV:
				mv.visitInsn(IDIV);
				mv.visitJumpInsn(GOTO, after);
				break;
			case MOD:
				mv.visitInsn(IREM);
				mv.visitJumpInsn(GOTO, after);
				break;
			case LT:
				mv.visitJumpInsn(IF_ICMPLT, pushTrue);
				break;
			case GT:
				mv.visitJumpInsn(IF_ICMPGT, pushTrue);
				break;
			case LE:
				mv.visitJumpInsn(IF_ICMPLE, pushTrue);
				break;
			case GE:
				mv.visitJumpInsn(IF_ICMPGE, pushTrue);
				break;
			case EQUAL:
				mv.visitJumpInsn(IF_ICMPEQ, pushTrue);
				break;
			case NOTEQUAL:
				mv.visitJumpInsn(IF_ICMPNE, pushTrue);
				break;
			case AND:
				mv.visitInsn(IAND);
				mv.visitJumpInsn(GOTO, after);
				break;
			case OR:
				mv.visitInsn(IOR);
				mv.visitJumpInsn(GOTO, after);
				break;
				
			default:
				assert false;
				break;
			}
		}
		else if(binaryExpression.getE0().getTypeName() == Type.TypeName.IMAGE && binaryExpression.getE1().getTypeName() == Type.TypeName.IMAGE) {
			if(binaryExpression.getOp().isKind(PLUS)) {
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "add", PLPRuntimeImageOps.addSig, false);
				mv.visitJumpInsn(GOTO, after);
			}
			else if(binaryExpression.getOp().isKind(MINUS)) {
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "sub", PLPRuntimeImageOps.subSig, false);
				mv.visitJumpInsn(GOTO, after);
			}
			else if(binaryExpression.getOp().isKind(EQUAL))
				mv.visitJumpInsn(IF_ACMPEQ, pushTrue);
			else if(binaryExpression.getOp().isKind(NOTEQUAL))
				mv.visitJumpInsn(IF_ACMPNE, pushTrue);
			else
				assert false;
		}
		else if(binaryExpression.getE0().getTypeName() == Type.TypeName.INTEGER && binaryExpression.getE1().getTypeName() == Type.TypeName.IMAGE) {
			mv.visitInsn(SWAP);
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mul", PLPRuntimeImageOps.mulSig, false);
		}
		else if(binaryExpression.getE0().getTypeName() == Type.TypeName.IMAGE && binaryExpression.getE1().getTypeName() == Type.TypeName.INTEGER) {
			if(binaryExpression.getOp().isKind(TIMES))
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mul", PLPRuntimeImageOps.mulSig, false);
			else if(binaryExpression.getOp().isKind(DIV))
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "div", PLPRuntimeImageOps.divSig, false);
			else if(binaryExpression.getOp().isKind(MOD))
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mod", PLPRuntimeImageOps.modSig, false);
			else
				assert false;
			
			mv.visitJumpInsn(GOTO, after);
		}
		else if(binaryExpression.getE0().getTypeName() == Type.TypeName.URL && binaryExpression.getE1().getTypeName() == Type.TypeName.URL) {
			if(binaryExpression.getOp().isKind(EQUAL))
				mv.visitJumpInsn(IF_ACMPEQ, pushTrue);
			else if(binaryExpression.getOp().isKind(NOTEQUAL))
				mv.visitJumpInsn(IF_ACMPNE, pushTrue);
			else
				assert false;
		}
		else if(binaryExpression.getE0().getTypeName() == Type.TypeName.FILE && binaryExpression.getE1().getTypeName() == Type.TypeName.FILE) {
			if(binaryExpression.getOp().isKind(EQUAL))
				mv.visitJumpInsn(IF_ACMPEQ, pushTrue);
			else if(binaryExpression.getOp().isKind(NOTEQUAL))
				mv.visitJumpInsn(IF_ACMPNE, pushTrue);
			else
				assert false;
		}
		else if(binaryExpression.getE0().getTypeName() == Type.TypeName.FRAME && binaryExpression.getE1().getTypeName() == Type.TypeName.FRAME) {
			if(binaryExpression.getOp().isKind(EQUAL))
				mv.visitJumpInsn(IF_ACMPEQ, pushTrue);
			else if(binaryExpression.getOp().isKind(NOTEQUAL))
				mv.visitJumpInsn(IF_ACMPNE, pushTrue);
			else
				assert false;
		}
		else
			assert false;
		
		mv.visitLdcInsn(0);
		mv.visitJumpInsn(GOTO, after);
		mv.visitLabel(pushTrue);
		mv.visitLdcInsn(1);
		mv.visitLabel(after);
		
		return null;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		//TODO  Implement this
		ArrayList<Dec> decs = block.getDecs();
		for(Dec dec : decs) {
			dec.visit(this, null);
			runDecs.add(dec);
		}
		
		Label start = new Label();
		mv.visitLabel(start);
		
		ArrayList<Statement> stmts = block.getStatements();
		for(Statement stmt : stmts) {
			stmt.visit(this, null);
			if(stmt instanceof BinaryChain)
				mv.visitInsn(POP);
		}
		
		Label end = new Label();
		mv.visitLabel(end);
		
		for(Dec dec : decs) {
			dec.setStart(start);
			dec.setEnd(end);
		}
		
		return null;
	}

	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
		//TODO Implement this
		mv.visitLdcInsn((booleanLitExpression.getValue()) ? 1 : 0);
		
		return null;
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
		if(constantExpression.getFirstToken().kind == KW_SCREENWIDTH)
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "getScreenWidth", PLPRuntimeFrame.getScreenWidthSig, false);
		else if(constantExpression.getFirstToken().kind == KW_SCREENHEIGHT)
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "getScreenHeight", PLPRuntimeFrame.getScreenHeightSig, false);
		else
			assert false;
		
		return null;
	}

	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {
		//TODO Implement this
		declaration.setSlotID(++runDecMaxSlot);
		
		if((Type.getTypeName(declaration.getType()) == FRAME) || Type.getTypeName(declaration.getType()) == IMAGE) {
			mv.visitInsn(ACONST_NULL);
			mv.visitVarInsn(ASTORE, declaration.getSlotID());
		}
		
		return null;
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
		if(filterOpChain.getFirstToken().isKind(OP_BLUR))
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, "blurOp", PLPRuntimeFilterOps.opSig, false);
		else if(filterOpChain.getFirstToken().isKind(OP_GRAY))
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, "grayOp", PLPRuntimeFilterOps.opSig, false);
		else if(filterOpChain.getFirstToken().isKind(OP_CONVOLVE))
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, "convolveOp", PLPRuntimeFilterOps.opSig, false);
		else
			assert false;
		
		return null;
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
		frameOpChain.getArg().visit(this, null);
		
		switch(frameOpChain.getFirstToken().kind) {
		case KW_SHOW:
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "showImage", PLPRuntimeFrame.showImageDesc, false);
			break;
		case KW_HIDE:
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "hideImage", PLPRuntimeFrame.hideImageDesc, false);
			break;
		case KW_MOVE:
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "moveFrame", PLPRuntimeFrame.moveFrameDesc, false);
			break;
		case KW_XLOC:
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "getXVal", PLPRuntimeFrame.getXValDesc, false);
			break;
		case KW_YLOC:
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "getYVal", PLPRuntimeFrame.getYValDesc, false);
			break;
			
		default:
			assert false;
			break;
		}
		
		return null;
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
		Dec target = identChain.getDec();
		
		if((int)arg == 0) {
			if(target instanceof ParamDec) {
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, className, target.getIdent().getText(), Type.getTypeName(target.getType()).getJVMTypeDesc());
			}
			else {
				switch(identChain.getTypeName()) {
				case INTEGER:
					mv.visitVarInsn(ILOAD, target.getSlotID());
					break;
				case BOOLEAN:
					mv.visitVarInsn(ILOAD, target.getSlotID());
					//mv.visitIntInsn(BIPUSH, target.getSlotID());
					break;
				case IMAGE: case FRAME:
					mv.visitVarInsn(ALOAD, target.getSlotID());
					break;
				
				default:
					assert false;
					break;
				}
			}
		}
		else if((int)arg == 1) {
			if(target instanceof ParamDec) {
				if(Type.getTypeName(target.getType()) == TypeName.INTEGER) {
					mv.visitInsn(DUP);
					mv.visitVarInsn(ALOAD, 0);
					mv.visitInsn(SWAP);
					mv.visitFieldInsn(PUTFIELD, className, target.getIdent().getText(), Type.getTypeName(target.getType()).getJVMTypeDesc());
					//CodeGenUtils.genPrint(DEVEL, mv, "", identChain.getTypeName());
				}
				else if(Type.getTypeName(target.getType()) == TypeName.FILE) {
					mv.visitVarInsn(ALOAD, 0);
					mv.visitFieldInsn(GETFIELD, className, target.getIdent().getText(), Type.getTypeName(target.getType()).getJVMTypeDesc());
					mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "write", PLPRuntimeImageIO.writeImageDesc, false);
				}
				else
					assert false;
			}
			else {
				switch(identChain.getTypeName()) {
				case INTEGER:
					mv.visitInsn(DUP);
					mv.visitVarInsn(ISTORE, target.getSlotID());
					//CodeGenUtils.genPrint(DEVEL, mv, "", identChain.getTypeName());
					break;
				case IMAGE:
					mv.visitInsn(DUP);
					mv.visitVarInsn(ASTORE, target.getSlotID());
					break;
				case FRAME:
					mv.visitVarInsn(ALOAD, target.getSlotID());
					mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "createOrSetFrame", PLPRuntimeFrame.createOrSetFrameSig, false);
					mv.visitInsn(DUP);
					mv.visitVarInsn(ASTORE, target.getSlotID());
					break;
					
				default:
					assert false;
					break;
				}
			}
		}
		else
			assert false;
		
		return null;
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
		//TODO Implement this
		Dec target = identExpression.getDec();
		
		if(target instanceof ParamDec) {
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, className, target.getIdent().getText(), Type.getTypeName(target.getType()).getJVMTypeDesc());
		}
		else {
			switch(identExpression.getTypeName()) {
			case INTEGER:
				mv.visitVarInsn(ILOAD, target.getSlotID());
				break;
			case BOOLEAN:
				mv.visitVarInsn(ILOAD, target.getSlotID());
				//mv.visitIntInsn(BIPUSH, target.getSlotID());
				break;
			case IMAGE: case FRAME:
				mv.visitVarInsn(ALOAD, target.getSlotID());
				break;
			
			default:
				assert false;
				break;
			}
		}
		
		return null;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
		//TODO Implement this
		Dec target = identX.getDec();
		
		if(target instanceof ParamDec) {
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(SWAP);
			mv.visitFieldInsn(PUTFIELD, className, target.getIdent().getText(), Type.getTypeName(target.getType()).getJVMTypeDesc());
		}
		else {
			switch(identX.getTypeName()) {
			case INTEGER:
				mv.visitVarInsn(ISTORE, target.getSlotID());
				break;
			case BOOLEAN:
				mv.visitIntInsn(ISTORE, target.getSlotID());
				break;
			case IMAGE: case FRAME: case URL: case FILE:
				mv.visitVarInsn(ASTORE, target.getSlotID());
				break;
			
			default:
				assert false;
				break;
			}
		}
		
		return null;
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		//TODO Implement this
		Label AFTER = new Label();
		
		ifStatement.getE().visit(this, null);
		mv.visitJumpInsn(IFEQ, AFTER);
		ifStatement.getB().visit(this, null);
		mv.visitLabel(AFTER);
		
		return null;
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
		imageOpChain.getArg().visit(this, null);
		
		if(imageOpChain.getFirstToken().isKind(OP_WIDTH)) {
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/awt/image/BufferedImage", "getWidth", PLPRuntimeImageOps.getWidthSig, false);
			//CodeGenUtils.genPrint(DEVEL, mv, "", imageOpChain.getTypeName());
		}
		else if(imageOpChain.getFirstToken().isKind(OP_HEIGHT)) {
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/awt/image/BufferedImage", "getHeight", PLPRuntimeImageOps.getHeightSig, false);
			//CodeGenUtils.genPrint(DEVEL, mv, "", imageOpChain.getTypeName());
		}
		else if(imageOpChain.getFirstToken().isKind(KW_SCALE))
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "scale", PLPRuntimeImageOps.scaleSig, false);
		else
			assert false;
		
		return null;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		//TODO Implement this
		mv.visitLdcInsn(intLitExpression.getValue());
		
		return null;
	}

	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
		//TODO Implement this
		//For assignment 5, only needs to handle integers and booleans
		if(Type.getTypeName(paramDec.getType()) == Type.TypeName.INTEGER) {
			cw.visitField(ACC_PUBLIC, paramDec.getIdent().getText(), Type.TypeName.INTEGER.getJVMTypeDesc(), null, 0);
			((MethodVisitor)arg).visitVarInsn(ALOAD, 1);
			((MethodVisitor)arg).visitLdcInsn(paramCount++);
			((MethodVisitor)arg).visitInsn(AALOAD);
			((MethodVisitor)arg).visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);
			((MethodVisitor)arg).visitVarInsn(ALOAD, 0);
			((MethodVisitor)arg).visitInsn(SWAP);
			((MethodVisitor)arg).visitFieldInsn(PUTFIELD, className, paramDec.getIdent().getText(), Type.TypeName.INTEGER.getJVMTypeDesc());
		}
		else if(Type.getTypeName(paramDec.getType()) == Type.TypeName.BOOLEAN) {
			cw.visitField(ACC_PUBLIC, paramDec.getIdent().getText(), Type.TypeName.BOOLEAN.getJVMTypeDesc(), null, false);
			((MethodVisitor)arg).visitVarInsn(ALOAD, 1);
			((MethodVisitor)arg).visitLdcInsn(paramCount++);
			((MethodVisitor)arg).visitInsn(AALOAD);
			((MethodVisitor)arg).visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z", false);
			((MethodVisitor)arg).visitVarInsn(ALOAD, 0);
			((MethodVisitor)arg).visitInsn(SWAP);
			((MethodVisitor)arg).visitFieldInsn(PUTFIELD, className, paramDec.getIdent().getText(), Type.TypeName.BOOLEAN.getJVMTypeDesc());
		}
		else if(Type.getTypeName(paramDec.getType()) == Type.TypeName.URL) {
			cw.visitField(ACC_PUBLIC, paramDec.getIdent().getText(), Type.TypeName.URL.getJVMTypeDesc(), null, false);
			((MethodVisitor)arg).visitVarInsn(ALOAD, 1);
			((MethodVisitor)arg).visitLdcInsn(paramCount++);
			((MethodVisitor)arg).visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "getURL", PLPRuntimeImageIO.getURLSig, false);
			((MethodVisitor)arg).visitVarInsn(ALOAD, 0);
			((MethodVisitor)arg).visitInsn(SWAP);
			((MethodVisitor)arg).visitFieldInsn(PUTFIELD, className, paramDec.getIdent().getText(), Type.TypeName.URL.getJVMTypeDesc());
		}
		else if(Type.getTypeName(paramDec.getType()) == Type.TypeName.FILE) {
			cw.visitField(ACC_PUBLIC, paramDec.getIdent().getText(), Type.TypeName.FILE.getJVMTypeDesc(), null, false);
			mv.visitTypeInsn(NEW, Type.TypeName.FILE.getJVMClass());
			mv.visitInsn(DUP);
			((MethodVisitor)arg).visitVarInsn(ALOAD, 1);
			((MethodVisitor)arg).visitLdcInsn(paramCount++);
			((MethodVisitor)arg).visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKESPECIAL, Type.TypeName.FILE.getJVMClass(), "<init>", "(Ljava/lang/String;)V", false);
			mv.visitVarInsn(ALOAD, 0);
			((MethodVisitor)arg).visitInsn(SWAP);
			((MethodVisitor)arg).visitFieldInsn(PUTFIELD, className, paramDec.getIdent().getText(), Type.TypeName.FILE.getJVMTypeDesc());
		}
		else
			assert false;
		
		return null;
	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		sleepStatement.getE().visit(this, null);
		mv.visitInsn(I2L);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "sleep", "(J)V", false);
		
		return null;
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {
		List<Expression> exps = tuple.getExprList();
		
		for(Expression e : exps)
			e.visit(this, null);
		
		return null;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
		//TODO Implement this
		Label GUARD = new Label();
		Label BODY = new Label();
		
		mv.visitJumpInsn(GOTO, GUARD);
		mv.visitLabel(BODY);
		whileStatement.getB().visit(this, null);
		mv.visitLabel(GUARD);
		whileStatement.getE().visit(this, null);
		mv.visitJumpInsn(IFNE, BODY);
		
		return null;
	}
}