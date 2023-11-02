using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;

namespace Napoleon
{
    class WinSizeHelper
    {
        static Dictionary<string, WinLayout> layouts = new Dictionary<string, WinLayout>();
        public static void Save(Window w, DataGrid grid)
        {
            string tag = w.GetType().FullName;
            WinLayout wl = new WinLayout(w, grid);
            layouts[tag] = wl;
        }

        public static void Resotre(Window w, DataGrid grid)
        {
            string tag = w.GetType().FullName;
            WinLayout wl;
            if(layouts.TryGetValue(tag, out wl))
            {
                wl.Set(w, grid);
            }
        }

        class ColumnLayout
        {
            public string header;
            public double width;

            public ColumnLayout(DataGridColumn c)
            {
                header = c.Header as string;
                width = c.Width.DisplayValue;
            }
        }

        class GridLayout
        {
            public double width;
            public double height;

            public List<ColumnLayout> columns = new List<ColumnLayout>();

            public GridLayout(DataGrid g)
            {
                width = g.Width;
                height = g.Height;

                foreach(DataGridColumn c in g.Columns)
                {
                    columns.Add(new ColumnLayout(c));
                }
            }

            public void Set(DataGrid g)
            {
                g.Height = height;
                g.Width = width;

                foreach(ColumnLayout cl in columns)
                {
                    foreach(DataGridColumn c in g.Columns)
                    {
                        if(c.Header as string == cl.header)
                        {
                            c.Width = new DataGridLength(cl.width);
                        }
                    }
                }
            }

        }
        class WinLayout
        {
            public double width;
            public double height;

            public GridLayout grid;

            public WinLayout(Window w, DataGrid g)
            {
                width = w.Width;
                height = w.Height;

                if (g != null)
                    grid = new GridLayout(g);
            }

            public void Set(Window w, DataGrid g)
            {
                if(w != null)
                {
                    w.Width = width;
                    w.Height = height;
                }
                if (g != null && grid != null)
                    grid.Set(g);
            }
        }
    }
}
