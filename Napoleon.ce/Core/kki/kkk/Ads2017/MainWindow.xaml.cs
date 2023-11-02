using GRSoft.Network;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Controls.Ribbon;
using System.Windows.Input;

namespace Ads2017
{
    public partial class MainWindow : RibbonWindow, Update.IDataLoadProcess
    {
        public static MainWindow window;

        public MainWindow()
        {
            ServerCommand.Category = "adsmanager";

            InitializeComponent();

            Loaded += MainWindow_Loaded;

            Properties.Settings cfg = Properties.Settings.Default;
            string ip = cfg.IP;
            int port = cfg.Port;

            ConnectionHelper.FindServer(ref ip, ref port, cfg.Login, "ADS2017\\Test;", true);

            window = this;



            progress.Items = new List<BandProgres.BandItem>
         {
            new BandProgres.BandItem() { color = TaskHelper.BkgItemColor(Task.RESOLVED), title=((App)Application.Current).resource.GetString("done") },
            new BandProgres.BandItem() { color = TaskHelper.BkgItemColor(Task.INWORK), title=((App)Application.Current).resource.GetString("inwork") },
            new BandProgres.BandItem() { color = TaskHelper.BkgItemColor(Task.NEW), title=((App)Application.Current).resource.GetString("undone")  }
         };

            progress.Title = ((App)Application.Current).resource.GetString("progress");

            list.OnItemClick = OnAgentSelected;
        }

        private void OnAgentSelected(object sender, object item)
        {
            if (((AgentItemView)item).StoredObject is TreeData sel)
            {
                timeLine.ClearTaksPanel();
                AddTaskPanels(sel);

                double cnt = sel.Done + sel.UnDone + sel.InWork;

                if (cnt != 0)
                    progress.Values = new double[] { sel.Done / cnt * 100, sel.InWork / cnt * 100, sel.UnDone / cnt * 100 };
                else
                    progress.Values = new double[] { 0, 0, 0 };

                progress.StartAnimation();
            }
        }

        private void CreateTaskEvent(object source, TicketGrid grid, TimeSpan time)
        {
            TaskWindow w = CreateTaskWindow();

            DateTime d = (calendar.SelectedDate ?? DateTime.Now).Date + time;

            TaskQuery t = new TaskQuery
            {
                taskid = Task.GenId(),
                userid = (string)grid.SourceObject,
                start = d,
                finish = d.AddHours(1),
                notify = 0,
                manager = ManagerHelper.Instance.CurrentUser.id
            };

            w.Stored = t;

            if ((w.ShowDialog() ?? false) && t.ActiveByDate(calendar.SelectedDate ?? DateTime.Now))
            {
                grid.AddTicket(CreateTicket(t));
                TaskHelper.Instance.AppendTask(t);
            }
        }

        void MainWindow_Loaded(object sender, RoutedEventArgs e)
        {
            calendar.SelectedDate = DateTime.Now;

            if (Properties.Settings.Default.Login.Length > 0)
                Refresh();
        }

        private void RefreshExecuted(object sender, ExecutedRoutedEventArgs e)
        {
            Refresh();
        }

        bool CheckMainData()
        {
            return ManagerHelper.Instance.CurrentUser != null;
        }

        public void SetManagerRights()
        {
            timeLine.TimeDblClick -= CreateTaskEvent;
            timeLine.TicketDblClick -= EditTaskEvent;
            timeLine.TicketDelete -= DeleteTaskEvent;
            timeLine.TicketPaste -= PasteTaskEvent;

            if (ManagerHelper.Instance.CurrentUser.CanWriteTask)
            {
                timeLine.TimeDblClick += CreateTaskEvent;
                timeLine.TicketDblClick += EditTaskEvent;
                timeLine.TicketDelete += DeleteTaskEvent;
                timeLine.TicketPaste += PasteTaskEvent;
            }
            else
            {
                //timeLine.TimeDblClick += CreateTaskEvent;
                timeLine.TicketDblClick += EditTaskEvent;
                //timeLine.TicketDelete += DeleteTaskEvent;
                //timeLine.TicketPaste += PasteTaskEvent;
            }
        }

        class MainUpd : Update.IDataLoadProcess
        {
            public void DoLoadData(Update.UpdateResult data) {
                if (!ManagerHelper.Instance.SetCurrentUserData(data))
                    return;
            }
            public UIElement[] GetRefreshControls() { return window.GetRefreshControls(); }
        }

        public void Refresh()
        {
            if(!CheckMainData())
            {
                Update.QueryList updA = new Update.QueryList();
                updA.Add(Agent.OBJECT_NAME, "hidden=0");
                updA.Add(Division.OBJECT_NAME, string.Empty);
                updA.Add(DivisionManager.OBJECT_NAME, DivisionManagerWhereStr());

                Update.StdDataRefresh(updA, new MainUpd());
                return;
            }

            Update.QueryList upd = new Update.QueryList();
            DateTime d1 = calendar.SelectedDate ?? DateTime.Now;
            DateTime d2 = d1.AddDays(1);

            string taskFilter = string.Format(Constants.TASK_FILTER, d1, d2, ManagerHelper.Instance.AgentsWhere(false));
            upd.Add(TaskQuery.OBJECT_NAME, taskFilter);
            upd.Add(TaskAttachment.OBJECT_INFO_NAME, taskFilter);
            upd.Add(AddressTemplate.OBJECT_NAME);


            Update.StdDataRefresh(upd, this);
        }

        private string DivisionManagerWhereStr()
        {
            return string.Format("\"login\" = '{0}' and \"password\" = '{1}'",
                Properties.Settings.Default.Login, Properties.Settings.Default.Password);
        }

        private void SettingExecuted(object sender, ExecutedRoutedEventArgs e)
        {
            SettingWindow sw = new SettingWindow();
            if(sw.ShowDialog() == true)
            {
                Refresh();
            }
        }

        public void DoLoadData(Update.UpdateResult data)
        {
            SetManagerRights();

            TaskHelper.Instance.CollectTasks(data.GetList<TaskQuery>(TaskQuery.OBJECT_NAME));
            TaskAttachmentHelper.Instance.Load(data.GetList<TaskAttachment>(TaskAttachment.OBJECT_INFO_NAME));
            CreateAgentListView();

            CommandManager.InvalidateRequerySuggested();
        }

        private void CreateAgentListView()
        {
            list.Adapter = new ListAdapter(CollectAgentData());
            list.SelectedIndex = 0;

            UserControl c = list.Adapter.GetView(0);
        }

        private class ListAdapter : ListViewAdapter
        {
            private List<TreeData> data = new List<TreeData>();

            public ListAdapter(List<TreeData> v)
            {
                this.data.AddRange(v);
            }

            public override int Count => data.Count;

            public override object GetItem(int position)
            {
                return data[position];
            }

            public override UserControl GetView(int position)
            {
                TreeData d = (TreeData)GetItem(position);

                AgentItemView view = new AgentItemView
                {
                    AgentName = d.Text,
                    StoredObject = d,
                };

                if (position == 0)
                    view.Visibility = Visibility.Collapsed;

                return view;
            }
        }

        private List<TreeData> CollectAgentData()
        {
            List<TreeData> result = new List<TreeData>();

            Division root = ManagerHelper.Instance.CurrentDivision;
            if (root == null)
                return result;

            NodeData n = new NodeData()
            {
                ID = string.Empty,
                Text = "<Все>",
                StoredObject = root
            };

            TreeData td = new TreeData(n, false) { IsSelected = true };
            result.Add(td);

            foreach (Agent a in DivisionAgents(root))
            {
                n = new NodeData()
                {
                    ID = a.id,
                    Text = a.name,
                    StoredObject = a
                };

                TreeData c = new TreeData(n, true);
                td.Childs.Add(c);
                result.Add(c);
            }

            return result;
        }

        private static List<Agent> DivisionAgents(Division d)
        {
            List<Agent> list = new List<Agent>();

            foreach (Division.DivisionAgent a in d.GetAllAgents())
                if (a != null && a.agent != null && !a.agent.Hidden)
                    list.Add(a.agent);

            list.Sort((x, y) => { return x.Name.CompareTo(y.Name); });

            return list;
        }

        public UIElement[] GetRefreshControls()
        {
            return new UIElement[] { btnRefresh };
        }

        private TicketGrid CreateTicketGrid(string caption, object source)
        {
            return timeLine.CreateTicketGrid(caption, source);
        }

        private void PasteTaskEvent(object source, TicketGrid grid, Ticket ticket, TimeCell where)
        {
            if (ticket.StoredObject is TaskQuery t)
                DoPastTask(grid, where, t);
        }

        private void DoPastTask(TicketGrid ticketGrid, TimeCell timeCell, TaskQuery task)
        {
            string oldid = task.taskid;
            task.taskid = Task.GenId();
            string oldUserID = task.userid;

            DateTime? dt = calendar.SelectedDate;

            if (dt != null)
            {
                TimeSpan s = timeCell.StartTime;
                DateTime start = dt.Value.Date + s;
                TimeSpan f = task.finish - task.start;
                DateTime finish = start + f;

                task.start = start;
                task.finish = finish;
                task.userid = (string)ticketGrid.SourceObject;
                task.solution = TaskQuery.NEW;

                UpdateCollection upd = new UpdateCollection();
                upd.Add(Task.OBJECT_NAME, task);

                UpdateCollection rem = new UpdateCollection();

                if (timeLine.CopyBuffer.Command == CopyBuffer.CommandType.Cut)
                {
                    rem.Add(Task.OBJECT_NAME, new Task { taskid = oldid });
                    TaskHelper.Instance.RemoveTask(oldUserID, task);
                }

                if (Update.WriteObjects(upd, rem) && task.ActiveByDate(calendar.SelectedDate ?? DateTime.Now))
                {
                    Ticket tk = new Ticket();
                    InitTicket(task, tk);
                    ticketGrid.AddTicket(tk);
                    TaskHelper.Instance.AppendTask(task);
                }
            }
        }

        private void DeleteTaskEvent(object source, Ticket ticket, TicketDeleteHandlerParam arg)
        {
            if (StdDialog.AskToDel(this))
            {
                TaskQuery k = (TaskQuery)ticket.StoredObject;
                arg.canDelete = RemoveTask(k);

                if (arg.canDelete)
                {
                    TaskHelper.Instance.RemoveTask(k.userid, k);
                }
            }
        }

        private void EditTaskEvent(object source, Ticket ticket)
        {
            TaskQuery t = (TaskQuery)ticket.StoredObject;
            TaskWindow w = CreateTaskWindow();
            w.Stored = t;

            if (w.ShowDialog().Value)
            {
                if (t.ActiveByDate(calendar.SelectedDate ?? DateTime.Now))
                {
                    InitTicket(t, ticket);
                    ticket.Redraw();
                }
                else
                {
                    ticket.Remove();
                    TaskHelper.Instance.RemoveTask(t.userid, t);
                }
            }
        }

        private bool RemoveTask(Task t)
        {
            return Update.WriteObjects(null, PrepareUpdate(t));
        }

        private static UpdateCollection PrepareUpdate(Task t)
        {
            UpdateCollection upd = new UpdateCollection();
            upd.Add(Task.OBJECT_NAME).Add(t);
            return upd;
        }

        private TaskWindow CreateTaskWindow()
        {
            TaskWindow result = new TaskWindow
            {
                Owner = this
            };

            return result;
        }

        private void TreeView_SelectedItemChanged(object sender, RoutedPropertyChangedEventArgs<object> e)
        {
            TreeData sel = e.NewValue as TreeData;
            timeLine.ClearTaksPanel();
            AddTaskPanels(sel);

            double cnt = sel.Done + sel.UnDone + sel.InWork;

            if (cnt != 0)
                progress.Values = new double[] { sel.Done / cnt * 100, sel.InWork / cnt * 100, sel.UnDone / cnt * 100 };
            else
                progress.Values = new double[] { 0, 0, 0 };

            progress.StartAnimation();
        }

        private void AddTaskPanels(TreeData sel)
        {
            if (sel != null)
            {
                if (sel.Childs.Count > 0)
                {
                    foreach (TreeData dc in sel.Childs)
                    {
                        if (dc.Childs.Count == 0 && dc.IsUser)
                            AddTaskPanel(dc.Text, dc.Node.ID);
                    }
                }
                else if (sel.IsUser)
                    AddTaskPanel(sel.Text, sel.Node.ID);
            }
        }

        private void AddTaskPanel(string text, object source)
        {
            TicketGrid grid = CreateTicketGrid(text, source);
            timeLine.AddTaskPanel(grid);

            foreach (TaskQuery t in TaskHelper.Instance.GetUserTasks((string)source))
            {
                Ticket tk = new Ticket();
                InitTicket(t, tk);
                grid.AddTicket(tk);
            }
        }

        private Ticket CreateTicket(TaskQuery t)
        {
            return InitTicket(t, new Ticket());
        }

        private Ticket InitTicket(TaskQuery t, Ticket tk)
        {
            tk.StartDateTime = t.start;
            tk.FinishDateTime = t.finish;
            tk.Text = TicketText(t);
            tk.StoredObject = t;
            tk.BorderColor = TaskHelper.BkgItemColor(t.solution);
            tk.Address = t.address;
            tk.BoldFont = t.solution == Task.NEW;
            tk.Attachments = TaskAttachmentHelper.Instance.HasAttach(t.taskid);

            return tk;
        }

        public string TicketText(Task t)
        {
            StringBuilder sb = new StringBuilder();

            if (sb.Length > 0 && t.text.Length > 0)
                sb.Append(", ");

            if (t.text.Length > 0)
                sb.Append(t.text);

            if (sb.Length == 0)
                sb.Append("Нет заговловка");

            return sb.ToString();
        }

        private static List<IDataSet> CreateUpdateList(Task task)
        {
            DataSet<string, Task> ds = new DataSet<string, Task>(Task.OBJECT_NAME, false);
            ds.Add(task.taskid, task);

            List<IDataSet> wrSet = new List<IDataSet>();
            wrSet.Add(ds);
            return wrSet;
        }

        private static void InitTask(Task task, TaskWindow w)
        {
            task.text = w.Text;
            task.start = w.StartDateTime;
            task.finish = w.FinishDateTime;
            task.fio = w.FIO;
            task.phone = w.Phone;
            task.client = w.Client;
            task.address = w.Address;
        }

        private void CanDeleteExecuted(object sender, CanExecuteRoutedEventArgs e)
        {
            CanTicketOperationExecuted(e);
        }

        private void DeleteExecuted(object sender, ExecutedRoutedEventArgs e)
        {
            timeLine.PerformDelete();
        }

        private void PasteExecuted(object sender, ExecutedRoutedEventArgs e)
        {
            timeLine.PerformPaste();
        }

        private void CanPasteExecuted(object sender, CanExecuteRoutedEventArgs e)
        {
            e.CanExecute = timeLine != null && !timeLine.CopyBuffer.IsEmpty;
        }

        private void CanTicketOperationExecuted(CanExecuteRoutedEventArgs e)
        {
            bool res = false;

            if (timeLine != null)
            {
                Ticket t = timeLine.GetTicket();

                if (t != null)
                {
                    res = ManagerHelper.Instance.CanEdit(((Task)t.StoredObject).manager);
                }
            }
            e.CanExecute = res;
        }

        private void CanCopyExecuted(object sender, CanExecuteRoutedEventArgs e)
        {
            CanTicketOperationExecuted(e);
        }

        private void CopyExecuted(object sender, ExecutedRoutedEventArgs e)
        {
            timeLine.PerformCopy();
        }

        private void CutExecuted(object sender, ExecutedRoutedEventArgs e)
        {
            timeLine.PerformCut();
        }

        private void CanCutExecuted(object sender, CanExecuteRoutedEventArgs e)
        {
            CanTicketOperationExecuted(e);
        }

        private void Calendar_SelectedDatesChanged(object sender, SelectionChangedEventArgs e)
        {
            timeLine.Date = ((Calendar)sender).SelectedDate ?? DateTime.MinValue;
            AdsCommands.Refresh.Execute(null, null);
        }

        private void JournalExecuted(object sender, ExecutedRoutedEventArgs e)
        {
            Window w = new TaskJournalWindow();
            w.Show();
        }

        private void AgentsInFieldsExecuted(object sender, ExecutedRoutedEventArgs e)
        {
            Window w = new UserLocationWindow();
            w.Show();
        }

        private void UserRouteExecuted(object sender, ExecutedRoutedEventArgs e)
        {
            Window w = new UserRouteWindow();
            w.Show();
        }

        private void DistanceExecuted(object sender, ExecutedRoutedEventArgs e)
        {
            Window w = new DistanceWindow();
            w.Show();
        }

        private void UserOrderExecuted(object sender, ExecutedRoutedEventArgs e)
        {
            Window w = new UserOrderWindow();
            w.Show();
        }

        private void HelpExecuted(object sender, ExecutedRoutedEventArgs e)
        {
            System.Diagnostics.Process.Start((string)Application.Current.FindResource("wiki"));
        }

        private void AboutExecuted(object sender, ExecutedRoutedEventArgs e)
        {
            Window w = new AboutWindow
            {
                Owner = this
            };
            w.Show();
        }

        private void UsersExecuted(object sender, ExecutedRoutedEventArgs e)
        {
            Window w = new UsersWindow();
            w.Show();
        }

        private void BtnTemplate_Click(object sender, RoutedEventArgs e)
        {
            new TemplateWindow().Show();
        }

        private void BtnAdmin_Click(object sender, RoutedEventArgs e)
        {
            new AdminLoginWindow().ShowDialog();
        }

        private void RibbonWindow_Closing(object sender, System.ComponentModel.CancelEventArgs e)
        {
            Update.CloseSession();
        }

        private void ResetAgentSelection(object sender, RoutedEventArgs e)
        {
            list.SelectedIndex = 0;
        }
    }

    class TreeData
    {
        private NodeData node;
        private ObservableCollection<TreeData> childs = new ObservableCollection<TreeData>();
        private bool isUser = false;

        public TreeData(NodeData node, bool isUser)
        {
            this.node = node;
            this.isUser = isUser;
        }

        public NodeData Node { get { return node; } set { node = value; } }
        public ObservableCollection<TreeData> Childs { get { return childs; } }
        public String Text { get { return node.Text; } }
        public bool IsUser { get { return isUser; } }
        public bool IsSelected { get; set; }
        public int Done { get { return TaskHelper.Instance.GetDoneTask(node.ID); } }
        public int UnDone { get { return TaskHelper.Instance.GetUnDoneTask(node.ID); } }
        public int InWork { get { return TaskHelper.Instance.GetInworkTask(node.ID); } }
    }

    class NodeData
    {
        public string Text { get; set; }
        public string ID { get; set; }
        public object StoredObject { get; set; }
    }
}
