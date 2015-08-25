package br.com.golive.sap.rfc;

public class EstruturaBase {
	
	private int offset;
	private String structName;
	
	public EstruturaBase(String structureName)
	{
		structName = structureName;
		offset = 0;
	}
	
	protected int getOffset(int fieldSize)
	{
		offset += fieldSize;
		return (offset);
	}
	
	public String getStructureName()
	{
		return (structName);
	}
}
