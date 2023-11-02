using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Windows.Forms;
using System.ComponentModel;
using System.Drawing;
using System.Collections;
using System.Reflection;

namespace GRSoft.NapoleonManager.Utils
{
   public delegate void AgentFilterHandler(Agent agents);

   class DataUtils
   {
      //Фильтр для общего набора данных
      public static readonly string USERID_IS_NULL_STR = "\"userid\" is null or \"userid\"=''";

      //Фильтр для общего набора данных Price
      // т.к берем ManagerPrice - он работает без фильтра
      public static readonly string COMMON_PRICE_FILTER_STR = USERID_IS_NULL_STR;// + " and SetQtyFilter(False)";

      //Фильтр из всех агентов
      //имеет вид "userid id("agent1","agent2","agent...")
      public static string MakeFilterFromAgents(AgentFilterHandler handler, Agents agents)
      {
         return MakeFilterFromAgents(handler, agents.Data);
      }

      public static string MakeFilterFromAgents(AgentFilterHandler handler, ICollection agents)
      {
         string param = "\"userid\" in(";

         foreach (Agent agent in agents)
         {
            if (agent == null)
               continue;

            param += "'" + agent.id + "',";

            if (handler != null)
            {
               handler(agent);
            }
         }

         param = param.Remove(param.Length - 1, 1);
         param += ")";

         return param;
      }

      public static string MakeFilterFromAgents(AgentFilterHandler handler, 
         List<GRSoft.NapoleonManager.Division.DivisionAgent> agents)
      {
         List<Agent> agentsList = new List<Agent>();

         foreach (GRSoft.NapoleonManager.Division.DivisionAgent ds in agents)
            if (ds != null && ds.agent != null)
               agentsList.Add(ds.agent);

         return MakeFilterFromAgents(handler, agentsList);
      }

      //Фильтр для набора Orders по дате
      public static string MakeCreatedDataFilter(DateTime begin, DateTime end)
      {
         return DataFilter("created", begin, end);
      }

      //Фильтр для набора UserLog по дате
      public static string MakeDateLogDataFilter(DateTime begin, DateTime end)
      {
         return DataFilter("date", begin, end);
      }

      public static string MakeUserLogObjDataFilter(DateTime begin, DateTime end)
      {
         return DataFilter("objDate", begin, end);
      } 

      //Фильтр из имени поля и даты
      private static string DataFilter(string field, DateTime begin, DateTime end)
      {
         return String.Format("\"{0}\" >= ToDate('{1:dd/MM/yyyy}') and \"{0}\" < ToDate('{2:dd/MM/yyyy}')",
               field,
               begin.Date,
               end.Date);
      }

      //public static DBConnection GetConnection()
      //{
      //   Config c = Config.Load();

      //   if (c.CheckLogin() == false)
      //      return null;

      //   return c.GetConnection();
      //}


      //Заполнить грид с одной колонкой объектом данных
      public static void FillGridFromDS(DataGridView dataGrid, DataGridViewColumn sortColumn, IDataSet dataSet)
      {
         if (dataSet != null)
         {
            //dataGrid.SuspendLayout();
            dataGrid.Enabled = false;

            try
            {
               dataGrid.Rows.Clear();

               foreach (object item in dataSet.Data)
               {
                  dataGrid.Rows.Add(new object[] { item });
               }

               dataGrid.Sort(sortColumn, ListSortDirection.Ascending);
            }
            finally
            {
               //dataGrid.ResumeLayout();
               dataGrid.Enabled = true;
            }
         }
      }

      //Начать драг и дроб на объекте грид
      public static void beginDragAndDropOnDataGrid<T>(DataGridView dataGridView, MouseEventArgs e)
      {
         if (e.Button == MouseButtons.Left)
         {
            DataGridView.HitTestInfo info = dataGridView.HitTest(e.X, e.Y);

            if (info.RowIndex >= 0 && info.ColumnIndex >= 0)
            {
               T item = (T)dataGridView.Rows[info.RowIndex].Cells[info.ColumnIndex].Value;
               if (item != null)
               {
                  dataGridView.DoDragDrop(item, DragDropEffects.Copy);
               }
            }
         }
      }

      //У объекта TreeNode получить узел верхнего уровня из узла
      public static TreeNode getTopParent(TreeNode node)
      {
         if (node != null)
         {
            while (node.Level > 0)
            {
               node = node.Parent;
            }
         }

         return node;
      }

      //Получить узел по координатам мышки
      public static TreeNode GetNodeFromPoint(TreeView treeView, Point point)
      {
         Point pos = treeView.PointToClient(point);
         return treeView.GetNodeAt(pos);
      }

      public static void GridSort<DataObjectType>(DataGridView grid, int columnIndex,
         GridBoundedObjectComparer comparer)
      {
         SortOrder sortOrder = grid.Columns[columnIndex].HeaderCell.SortGlyphDirection;

         foreach (DataGridViewColumn column in grid.Columns)
            column.HeaderCell.SortGlyphDirection = SortOrder.None;

         sortOrder = sortOrder == SortOrder.Ascending ? SortOrder.Descending : SortOrder.Ascending;

         IList list = (IList)grid.DataSource;

         comparer.DataPropertyName = grid.Columns[columnIndex].DataPropertyName;
         comparer.Direction = sortOrder;

         if (list != null)
            ArrayList.Adapter(list).Sort(comparer);

         grid.Refresh();
         grid.Columns[columnIndex].HeaderCell.SortGlyphDirection = sortOrder;
      }
   }

   public abstract class GridBoundedObjectComparer : IComparer
   {
      private string dataPropertyName;
      private SortOrder direction;

      public string DataPropertyName { get { return dataPropertyName; } set { dataPropertyName = value; } }
      public SortOrder Direction { get { return direction; } set { direction = value; } }

      public virtual int Compare(object x, object y)
      {
         if (direction == SortOrder.None)
            return 0;

         PropertyInfo f1 = x.GetType().GetProperty(dataPropertyName, BindingFlags.Public | BindingFlags.Instance | BindingFlags.GetProperty);
         PropertyInfo f2 = y.GetType().GetProperty(dataPropertyName, BindingFlags.Public | BindingFlags.Instance | BindingFlags.GetProperty);
         object val1 = f1.GetValue(x, null);
         object val2 = f2.GetValue(y, null);

         if (val1 == null && val2 == null)
            return 0;
         else if (val1 == null)
            return -1;
         else if (val2 == null)
            return 1;
         else if (val1 is IComparable)
            return ((IComparable)val1).CompareTo(val2) * (Direction == SortOrder.Ascending ? 1 : -1);
         else
            return 0;
      }
   }

   public interface FindObject : IEnumerable
   {
      Direction Dir { get; set;}
      String Text { get; set; }
      void Select(IEnumerator iter);
   }

   public class SearchEngine
   {
      private FindObject findObject;
      private IEnumerator iter;

      public SearchEngine(FindObject findObject)
      {
         this.findObject = findObject;
         iter = findObject.GetEnumerator();
      }

      public bool find(String text, Direction dir)
      {
         findObject.Dir = dir;
         findObject.Text = text;
         
         bool result = iter.MoveNext();

         if (result)
            findObject.Select(iter);
         else
            iter = findObject.GetEnumerator();

         return result;
      }
   }

   public class FindDataGridObject : FindObject
   {
      private DataGridView dgv;
      private int columnIndex;
      private Direction direction = Direction.DOWN;
      private String text = String.Empty;
      private int pos;

      public Direction Dir { get { return direction; } set { direction = value; } }
      public String Text { get { return text; } set { text = value; } }

      public FindDataGridObject(DataGridView dgv, int columnIndex)
      {
         this.dgv = dgv;
         this.columnIndex = columnIndex;
      }

      public IEnumerator GetEnumerator()
      {
         int start = pos;
         while(true)
         {
            DataGridViewRow row = Next();
            string val = row.Cells[columnIndex].Value.ToString().ToUpper();

            if (val.Contains(text.ToUpper()))
               yield return row;

            if (pos == start)
               yield break;
         }
      }

      private DataGridViewRow Next()
      {
         pos = direction == Direction.DOWN ? pos + 1 : pos - 1;

         if (pos < 0)
            pos = dgv.Rows.Count - 1;
         else if (pos >= dgv.Rows.Count)
            pos = 0;

         return dgv.Rows[pos];
      }

      public void Select(IEnumerator iter)
      {
         if (iter != null)
         {
            int pos = ((DataGridViewRow)iter.Current).Index;
            dgv.CurrentCell = dgv.Rows[pos].Cells[0];
            ((DataGridViewRow)iter.Current).Selected = true;
         }
      }
   }

   //Период для дат с..по...
   public class DatePeriod
   {
      DateTime from;
      DateTime till;

      public DatePeriod(DateTime from, DateTime till)
      {
         this.from = from;
         this.till = till;
      }

      public DateTime From { get { return from; } }
      public DateTime Till { get { return till; } }

      //Проверка на то что проверяемый "период" пересекается с объектом
      public bool IsIntersect(DatePeriod period)
      {
         bool a = period.from >= from && period.from <= till ||
            period.till >= from && period.till <= till;

         bool b = period.from <= from && period.till >= from ||
            period.from <= till && period.till >= till;

         return a || b;
      }
   }

   public class CheckObj
   {
      private string msg = string.Empty;
      private object chObj;

      public CheckObj() { }
      public CheckObj(string msg, object chObj)
      {
         this.msg = msg;
         this.chObj = chObj;
      }

      public string Mgs { get { return msg; } }
      public object ChObj { get { return chObj; } }
   }

   interface IChecker
   {
      bool Check(object checkObject, out CheckObj outObject);
   }

   class DatePeriodChecker : IChecker
   {
      private List<DatePeriod> dateList;

      public DatePeriodChecker(List<DatePeriod> dateList)
      {
         this.dateList = dateList;
      }

      public bool Check(object checkObject, out CheckObj outObject)
      {
         outObject = null;
         const string MSG = "Период дат пересекается или лежит внутри другого периода";

         foreach (DatePeriod dp in dateList)
            if (dp.IsIntersect((DatePeriod)checkObject))
            {
               outObject = new CheckObj(MSG, dp);
               return false;
            }

         return true;
      }
   }

}
