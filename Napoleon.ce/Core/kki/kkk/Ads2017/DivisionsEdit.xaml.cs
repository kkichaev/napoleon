using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Controls.Primitives;
using System.Windows.Controls.Ribbon;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;

namespace Ads2017
{
    /// <summary>
    /// Логика взаимодействия для DivisionsEdit.xaml
    /// </summary>
    public partial class DivisionsEdit : RibbonWindow, Update.IDataLoadProcess
    {
        int maxID = 0;
        const string ADMPWD = "admpwd";

        FrameworkElement curFocused;

        Dictionary<int, Division> rmvDivision = new Dictionary<int, Division>();
        Dictionary<string, DivisionManager> rmvManagers = new Dictionary<string, DivisionManager>();
        Dictionary<string, Agent> rmvAgents = new Dictionary<string, Agent>();
        private bool passwordChanged;

        public DivisionsEdit()
        {
            InitializeComponent();

            dgvManagers.CanUserAddRows = false;
            dgvAgents.CanUserAddRows = false;
        }

        private void SaveExecuted(object sender, ExecutedRoutedEventArgs e)
        {
            Save();
        }

        private void RefreshExecuted(object sender, ExecutedRoutedEventArgs e)
        {
            Refresh();
        }

        private void DeleteExecuted(object sender, ExecutedRoutedEventArgs e)
        {
            Division sel = tvDivisions.SelectedItem as Division;
            if (sel == null)
                return;

            FrameworkElement cur = curFocused;
            if (cur == tvDivisions)
            {
                Division root = tvDivisions.Items[0] as Division;
                if (root.RemoveRecurs(sel))
                {
                    rmvDivision[sel.id] = sel;
                }
            }
            else if (cur == dgvAgents)
            {
                Division.DivisionAgent rmv = dgvAgents.SelectedItem as Division.DivisionAgent;
                if(rmv != null)
                {
#if EXTERNAL_AGENTS
#else
                    rmvAgents[rmv.id] = rmv.agent;
#endif
                    sel.Agents.Remove(rmv);
                    sel.Dirty = true;
                }
            }
            else if (cur == dgvManagers)
            {
                DivisionManager rmv = dgvManagers.SelectedItem as DivisionManager;
                if (rmv != null)
                {
                    sel.Managers.Remove(rmv);
                    if(rmv.Login.Length > 0)
                        rmvManagers[rmv.Login] = rmv;
                    sel.Dirty = true;
                }
            }
        }

        private void SelectDivision(ItemsControl parentContainer, Division d)
        {
            TreeViewItem tv = parentContainer.ItemContainerGenerator.ContainerFromItem(d) as TreeViewItem;
            if (tv != null)
            {
                tv.IsSelected = true;
                return;
            }

            foreach (object item in parentContainer.Items)
            {
                TreeViewItem currentContainer = parentContainer.ItemContainerGenerator.ContainerFromItem(item) as TreeViewItem;
                if (currentContainer != null && currentContainer.Items.Count > 0)
                {
                    currentContainer.IsExpanded = true;
                    if (currentContainer.ItemContainerGenerator.Status != GeneratorStatus.ContainersGenerated)
                    {
                        currentContainer.ItemContainerGenerator.StatusChanged += delegate
                        {
                            SelectDivision(currentContainer, d);
                        };
                    }
                    else
                    {
                        SelectDivision(currentContainer, d);
                    }
                }
            }
        }

        private void AddExecuted(object sender, ExecutedRoutedEventArgs e)
        {
            Division sel = tvDivisions.SelectedItem as Division;
            if (sel == null)
                return;

            FrameworkElement cur = curFocused;
            if(cur == tvDivisions)
            {
                Division d = new Division();
                d.Name = Properties.Resources.new_division;
                d.id = ++maxID;
                d.parent = sel.id;
                sel.Childs.Add(d);
                sel.Dirty = true;

                TreeViewItem tvi = tvDivisions.ItemContainerGenerator.ContainerFromItem(tvDivisions.Items[0]) as TreeViewItem;
                if(!tvi.IsExpanded)
                {
                    tvi.IsExpanded = true;
                    if (tvi.ItemContainerGenerator.Status != GeneratorStatus.ContainersGenerated)
                        tvi.ItemContainerGenerator.StatusChanged += delegate
                        {
                            SelectDivision(tvi, d);
                        };
                    else
                        SelectDivision(tvi, d);
                } else
                    SelectDivision(tvi, d);
            }
            else if(cur == dgvAgents)
            {
#if EXTERNAL_AGENTS
                SelectDivisionAgents f = new SelectDivisionAgents();
                f.SetSelectedAgents(sel, sel.Agents);
                f.SelectAgents += OnSelectAgents;
                f.ShowDialog();
#else
                Division.DivisionAgent a = new Division.DivisionAgent();
                a.Name = Properties.Resources.new_agent;
                a.agent.id = Guid.NewGuid().ToString().Replace("-", "");
                a.id = a.agent.id;

                sel.Agents.Add(a);
#endif
                sel.Dirty = true;
            }
            else if(cur == dgvManagers)
            {
                DivisionManager mgr = new DivisionManager();
                mgr.Name = Properties.Resources.new_manager;
                mgr.division = sel.id;
                mgr.id = Guid.NewGuid().ToString().Replace("-", "");
                sel.Managers.Add(mgr);
                sel.Dirty = true;
            }
        }

        void RemoveAgents(Division cur, Division exclude, List<Agent> agents)
        {
            if (cur != exclude)
                cur.Remove(agents);
            foreach (Division ch in cur.Childs)
                RemoveAgents(ch, exclude, agents);
        }

        private void OnSelectAgents(object sender, AgentSelectedArgs arg)
        {
            Dictionary<string, Division.DivisionAgent> used = new Dictionary<string, Division.DivisionAgent>();
            Division d = arg.division;

            foreach (Division.DivisionAgent da in d.Agents)
                used[da.id] = da;

            foreach(Agent agent in arg.agents)
            {
                if(used.ContainsKey(agent.id))
                {
                    used.Remove(agent.id);
                } else
                {
                    Division.DivisionAgent a = new Division.DivisionAgent()
                    {
                        agent = agent,
                        id = agent.id
                    };

                    d.Agents.Add(a);
                }
            }

            foreach (Division.DivisionAgent da in used.Values)
                d.Agents.Remove(da);

            Division root = tvDivisions.Items[0] as Division;
            RemoveAgents(root, d, arg.agents);

            d.Dirty = true;
        }

        private void Refresh()
        {
            Update.QueryList upd = new Update.QueryList();
            upd.Add(Agent.OBJECT_NAME);
            upd.Add(DivisionManager.OBJECT_NAME);
            upd.Add(Division.OBJECT_NAME);
            upd.Add(CommonConfig.OBJECT_NAME);

            Update.StdDataRefresh(upd, this, true);
        }

        void LoadDivisionTree(List<Division> divisions, List<DivisionManager> managers)
        {
            Division d = Division.PrepareTree(divisions, managers);
            if(d != null)
            {
                List<Division> src = new List<Division>(new Division[] { d });
                tvDivisions.ItemsSource = src;
                d.ClearDirty();
            }
        }

        public void DoLoadData(Update.UpdateResult data)
        {
            List<Division> divisions = data.GetList<Division>(Division.OBJECT_NAME);
            foreach (Division d in divisions)
                if (maxID < d.id)
                    maxID = d.id;
            LoadDivisionTree(divisions, data.GetList<DivisionManager>(DivisionManager.OBJECT_NAME));

            Dictionary<string, CommonConfig> cgf = data.GetDictionary<CommonConfig>(CommonConfig.OBJECT_NAME);

            if (cgf.ContainsKey(ADMPWD))
            {
                string val = cgf[ADMPWD].value;

                if (val.Trim().Length > 0)
                    tbAdmpwd.Text = val;
            }

            btnSave.IsEnabled = false;
        }

        public UIElement[] GetRefreshControls()
        {
            return new UIElement[] { btnRefresh, btnSave };
        }

        private void RibbonWindow_Loaded(object sender, RoutedEventArgs e)
        {
            Refresh();
        }

        private void RibbonWindow_Closing(object sender, System.ComponentModel.CancelEventArgs e)
        {
            if (IsDirty())
            {
                MessageBoxResult res = StdDialog.AskToSave(this);
                if (res == MessageBoxResult.Cancel)
                {
                    e.Cancel = true;
                    return;
                }
                if (res == MessageBoxResult.Yes)
                    Save();
            }

            e.Cancel = false;
        }

        bool IsDirty()
        {
            bool res = false;

            if (tvDivisions != null && tvDivisions.Items != null && tvDivisions.Items.Count > 0)
            {
                Division d = tvDivisions.Items[0] as Division;
                res = d.IsDirty();
            }

            return res || passwordChanged;
        }

        void LoadUpdateLists(Division d, List<Division> divs, List<DivisionManager> mgrs, List<Agent> agents)
        {
            foreach (DivisionManager dm in d.Managers)
            {
                if(dm.Login.Length != 0)
                    mgrs.Add(dm);
            }

            foreach (Division.DivisionAgent da in d.Agents)
                agents.Add(da.agent);

            divs.Add(d);
            foreach(Division ch in d.Childs)
                LoadUpdateLists(ch, divs, mgrs, agents);
        }

        void CheckDivisionFormat()
        {
            Format f = Format.Find(Division.OBJECT_NAME);
            if(f == null)
            {
                f = new Format(Division.OBJECT_NAME + "$agents");
                f.Add(new StringFormat("id"));

                Format.Add(f);

                f = new Format(Division.OBJECT_NAME);
                f.Add(new NumberFormat("id"));
                f.Add(new StringFormat("name"));
                f.Add(new StringFormat("description"));
                f.Add(new NumberFormat("parent"));
                f.Add(new ObjectFormat("agents", Division.OBJECT_NAME));

                Format.Add(f);
            }
        }

        bool Save()
        {
            if(tvDivisions.Items.Count == 0)
            {
                return true;
            }

            Division d = tvDivisions.Items[0] as Division;
            List<Division> divs = new List<Division>();
            List<DivisionManager> mgrs = new List<DivisionManager>();
            List<Agent> agents = new List<Agent>();

            dgvAgents.CommitEdit(DataGridEditingUnit.Row, true);
            dgvManagers.CommitEdit(DataGridEditingUnit.Row, true);

            LoadUpdateLists(d, divs, mgrs, agents);

            UpdateCollection write = new UpdateCollection();
            write.Add(DivisionManager.OBJECT_NAME, mgrs);
            write.Add(Division.OBJECT_NAME, divs);

            List<CommonConfig> cfgs = new List<CommonConfig>();
            CommonConfig cc = new CommonConfig
            {
                key = ADMPWD,
                value = tbAdmpwd.Text.Trim()
            };

            cfgs.Add(cc);
            write.Add(CommonConfig.OBJECT_NAME, cfgs);

#if EXTERNAL_AGENTS
#else
            foreach(Agent ag in rmvAgents.Values)
            {
                ag.hidden = 1;
                agents.Add(ag);
            }
#endif
            write.Add(Agent.OBJECT_NAME, agents);

            UpdateCollection remove = new UpdateCollection();
            remove.Add(DivisionManager.OBJECT_NAME, rmvManagers.Values);
            remove.Add(Division.OBJECT_NAME, rmvDivision.Values);

            CheckDivisionFormat();

            if(Update.WriteObjects(write, remove, true))
            {
                rmvAgents.Clear();
                rmvDivision.Clear();
                rmvManagers.Clear();

                d.ClearDirty();
                return true;
            }
            return false;
        }

        private void TvDivisions_SelectedItemChanged(object sender, RoutedPropertyChangedEventArgs<object> e)
        {
            ObservableCollection<Division.DivisionAgent> agents = new ObservableCollection<Division.DivisionAgent>();
            ObservableCollection<DivisionManager> managers = new ObservableCollection<DivisionManager>();

            Division d = e.NewValue as Division;
            if( d != null)
            {
                agents = d.Agents;
                managers = d.Managers;

                bool svDirty = d.Dirty;
                tbDivision.Text = d.Name;
                d.Dirty = svDirty;
            }

            dgvAgents.ItemsSource = agents;
            dgvManagers.ItemsSource = managers;
        }

        void SetCurrent(FrameworkElement el, String add_title, String remove_title)
        {
            curFocused = el;
            btnAdd.Label = add_title;
            btnDel.Label = remove_title;
        }

        private void TvDivisions_GotFocus(object sender, RoutedEventArgs e)
        {
            SetCurrent(tvDivisions, Properties.Resources.add_division, Properties.Resources.del_division);
        }

        private void DgvManagers_GotFocus(object sender, RoutedEventArgs e)
        {
            SetCurrent(dgvManagers, Properties.Resources.add_manager, Properties.Resources.del_manager);
        }

        private void DgvAgents_GotFocus(object sender, RoutedEventArgs e)
        {
            SetCurrent(dgvAgents, Properties.Resources.add_agent, Properties.Resources.del_agent);
        }

        private void DgvAgents_MouseDown(object sender, MouseButtonEventArgs e)
        {
            SetCurrent(dgvAgents, Properties.Resources.add_agent, Properties.Resources.del_agent);
        }

        private void DgvManagers_MouseDown(object sender, MouseButtonEventArgs e)
        {
            SetCurrent(dgvManagers, Properties.Resources.add_manager, Properties.Resources.del_manager);
        }

        private void TvDivisions_MouseDown(object sender, MouseButtonEventArgs e)
        {
            SetCurrent(tvDivisions, Properties.Resources.add_division, Properties.Resources.del_division);
        }

        private void TbDivision_TextChanged(object sender, TextChangedEventArgs e)
        {
            Division cur = tvDivisions.SelectedItem as Division;
            if(cur != null)
            {
                cur.Name = tbDivision.Text;
                cur.Dirty = true;
            }
        }

        private void tbAdmpwd_KeyUp(object sender, KeyEventArgs e)
        {
            passwordChanged = true;
            CommandManager.InvalidateRequerySuggested();
        }

        private void CanSaveExecute(object sender, CanExecuteRoutedEventArgs e)
        {
            e.CanExecute = IsDirty();
        }
    }
}