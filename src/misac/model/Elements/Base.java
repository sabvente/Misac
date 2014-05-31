package misac.model.Elements;


public class Base extends Element {
	private static final long serialVersionUID = 1L;

	public Base() {
		elementType = ElementType.Base;
	}
	
	@Override
	public byte getDefaultPower()
	{
		return 8;
	}
}
