package com.grsoft.napoleon.documents;

import java.util.Comparator;
import java.util.Map.Entry;

public class CmpHistory implements Comparator<Entry<Long, Integer>>
{
	@Override
	public int compare(Entry<Long, Integer> object1, Entry<Long, Integer> object2)
	{
		 if (object2.getKey() - object1.getKey() < 0)
			 return -1;
		 if (object2.getKey() - object1.getKey() > 0)
			 return 1;
		 return 0;
	}
}