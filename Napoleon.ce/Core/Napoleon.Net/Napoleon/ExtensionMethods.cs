using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Napoleon
{
    public static class ExtensionMethods
    {
        public delegate bool Equ<T>(T val);

        public static void ForEach<T>(this IEnumerable<T> enumerable, Action<T> action)
        {
            foreach (var item in enumerable)
            {
                action(item);
            }
        }

        public static void ForEachFilter<T>(this IEnumerable<T> enumerable, Action<T> action, Equ<T> cmp)
        {
            foreach (var item in enumerable)
            {
                if (cmp(item))
                    action(item);
            }
        }
    }
}
