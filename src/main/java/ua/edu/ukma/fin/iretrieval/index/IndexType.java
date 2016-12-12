package ua.edu.ukma.fin.iretrieval.index;

public enum IndexType
{
	BASIC("Basic"), INVERTED("Inverted"), VECTOR_SPACE("Vector space"), ZONED("Zoned");
	
	private IndexType(String readableName) {
		this.readableName = readableName;
	}
	
	public String getReadableName()
	{
		return readableName;
	}

	private String readableName = "";
}
