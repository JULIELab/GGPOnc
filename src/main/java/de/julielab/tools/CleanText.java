package de.julielab.tools;

public class CleanText
{
	public static String reviseText(String text)
	{
		if (text.startsWith("\n"))
		{
			text = text.replaceFirst("\n", "");
		}
		if (text.startsWith(" "))
		{
			text = text.replaceFirst(" +", "");
		}

		text = text.replaceAll("\\*", " * ");

		text = text.replaceAll("\\,+", ",");
		text = text.replaceAll("\\,\\.", ".");
		text = text.replaceAll("\\r\\n- ?\\r\\n", "- ");
		text = text.replaceAll("\\n- ?\\n", "- ");
		text = text.replaceAll("\\n-", "\n- ");

		text = text.replaceAll("\\n +", "\n");
		text = text.replaceAll("\\n+", "\n");
		text = text.replaceAll(" +", " ");
		
		text = text.replaceAll("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)?", "[URL]");

		text = text.trim();
		return text;
	}
}
