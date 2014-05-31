package misac.model.Elements;

import java.io.Serializable;


/**
 * Base of all elements
 * @author Szabó Levente
 *
 */
public abstract class Element implements Serializable
{
	private static final long serialVersionUID = 1L;
	private boolean visible = false;

	protected ElementType elementType = ElementType.Empty;
	protected ElementOwner owner = ElementOwner.Map;
	protected byte power = 1;

	public boolean isOvercome(ElementType other)
	{
		return false;
	}

	public boolean isGenerate(ElementType other)
	{
		return false;
	}

	public ElementType getElementType()
	{
		return elementType;
	}

	public byte getPower()
	{
		return power;
	}

	public void setPower(byte power)
	{
		this.power = power;
	}

	public byte getDefaultPower()
	{
		return 1;
	}

	public ElementOwner getOwner()
	{
		return owner;
	}

	public void setOwner(ElementOwner owner)
	{
		this.owner = owner;
	}
	
	public boolean isVisible()
	{
		return visible;
	}
	
	public void setVisibiliy(boolean visible)
	{
		this.visible = visible;
	}

	public static Element fromTypeFactory(ElementType type)
	{
		switch (type)
		{
		case Base:
			return new Base();
		case Empty:
			return new Empty();
		case Magical:
			return new Magical();
		case Earth:
			return new Earth();
		case Fire:
			return new Fire();
		case Wood:
			return new Tree();
		case Water:
			return new Water();
		case Metal:
			return new Metal();
		default:
			break;
		}
		return null;
	}

	public static Element fromTypeAndOwnerFactory(ElementType type, ElementOwner owner)
	{
		Element e = fromTypeFactory(type);
		e.setOwner(owner);
		return e;
	}
}
