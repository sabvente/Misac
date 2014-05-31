package misac.model.Elements;


public class Fire extends Element {
	private static final long serialVersionUID = 1L;

	public Fire()
	{
		elementType = ElementType.Fire;
	}

	@Override
	public boolean isOvercome(ElementType other) {
		if(other == ElementType.Metal || other == ElementType.Base)
			return true;
		return false;
	}

	@Override
	public boolean isGenerate(ElementType other) {
		if(other == ElementType.Earth)
			return true;
		return false;
	}
}
