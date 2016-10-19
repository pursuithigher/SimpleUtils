package com.views.ui.customview.slidbar;

import java.util.Comparator;

/**
 * this class is to compare the first pingyin
 */
public class PinyinComparator implements Comparator<SortModel>
{

	public int compare(SortModel o1, SortModel o2)
	{
		if (o1.getSortLetter().equals("@") || o2.getSortLetter().equals("#"))
		{
			return -1;
		} else if (o1.getSortLetter().equals("#") || o2.getSortLetter().equals("@"))
		{
			return 1;
		} else
		{
			return o1.getSortLetter().compareTo(o2.getSortLetter());
		}
	}

}