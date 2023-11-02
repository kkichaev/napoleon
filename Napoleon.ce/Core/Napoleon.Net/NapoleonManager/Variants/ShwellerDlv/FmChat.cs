using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Diagnostics;
using System.Drawing;
using System.Reflection;
using System.Text;
using System.Threading;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmChat : Form
   {
      private SimpleDataSet<ChatQuery> dsChatData = new SimpleDataSet<ChatQuery>(ChatQuery.OBJECT_NAME, true);
      private DataSet<string, ChatGroup> dsGroup = new DataSet<string, ChatGroup>(ChatGroup.OBJECT_NAME);
      private DataSet<string, ChatUser> dsChatUser;
      private System.Threading.Timer refreshTimer;
      private Dictionary<string, DateTime> groupEvent = new Dictionary<string, DateTime>();

      public FmChat()
      {
         InitializeComponent();

         dsChatUser = (DataSet<string, ChatUser>)DataModule.Get(ChatUser.OBJECT_NAME) ?? new DataSet<string, ChatUser>(ChatUser.OBJECT_NAME);
         wb.DocumentText = "";
      }

      void refreshTimer_Tick(object sender)
      {
         List<IDataSet> list = new List<IDataSet>();
         list.Add(dsChatUser);
         list.Add(dsGroup);
         list.Add(dsChatData);
         DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), list, FmWait.ProgressIndicator).Join();
         BeginInvoke(new EmptyParamHandler(DoLoadData));
      }

      private void DoLoadData()
      {
         foreach (ChatData d in dsChatData.Values)
         {
            if (!groupEvent.ContainsKey(d.group) || groupEvent[d.group] < d.created)
               groupEvent[d.group] = d.created;
         }

         LoadGroup();
         LoadChat();
      }

      private void LoadChat()
      {
         ChatGroup g = lbGroup.SelectedItem as ChatGroup;
         StringBuilder sb = new StringBuilder();

         if (g != null)
         {
            List<ChatQuery> list = new List<ChatQuery>();
            list.AddRange(dsChatData.Values);
            list.Reverse();

            foreach (ChatData d in list)
            {
               if (d.group.Equals(g.id))
               {
                  if(sb.ToString().Trim().Length > 0)
                     sb.Append("<br>");

                  sb.Append(d.ToString());
               }
            }
         }

         WriteChatText(sb.ToString());
      }

      private void WriteChatText(string text)
      {
         Control c = ActiveControl;
         if (text.Length == 0)
            text = "&nbsp;";

         if (wb.Document != null)
         {
            wb.Document.OpenNew(true);
            wb.Document.Write(text);

            if (wb.Document.Body != null)
               wb.Document.Window.ScrollTo(0, wb.Document.Body.ScrollRectangle.Height);
         }

         ActiveControl = c;
      }

      private void LoadGroup()
      {
         lbGroup.SuspendLayout();
         ChatGroup sel = lbGroup.SelectedItem as ChatGroup;

         lbGroup.Items.Clear();
         List<ChatGroup> list = new List<ChatGroup>();

         foreach (ChatGroup g in dsGroup.Values)
            list.Add(g);

         list.Sort(CmpChatGroup);
         lbGroup.Items.AddRange(list.ToArray());
         lbGroup.SelectedItem = sel;

         if (sel == null && lbGroup.Items.Count > 0)
            lbGroup.SelectedIndex = 0;

         lbGroup.ResumeLayout();
      }

      private int CmpChatGroup(ChatGroup x, ChatGroup y)
      {
         DateTime lhs = groupEvent.ContainsKey(x.id) ? groupEvent[x.id] : DateTime.MinValue;
         DateTime rhs = groupEvent.ContainsKey(y.id) ? groupEvent[y.id] : DateTime.MinValue;

         return lhs.CompareTo(rhs) * -1;
      }

      private void FmChat_FormClosed(object sender, FormClosedEventArgs e)
      {
         refreshTimer.Dispose(); 
      }

      private void btnSend_Click(object sender, EventArgs e)
      {
         string msg = tbText.Text.Trim();
         ChatGroup g = lbGroup.SelectedItem as ChatGroup;

         if(g != null)
         {
            string gid = g.id;
            if (msg.Length > 0)
            {
               ChatData c = new ChatData();
               c.id = GRSoft.Network.DataObject.GenId();
               c.text = msg;
               c.userid = CurrentUser.user.User.id;
               c.created = DateTime.Now;
               c.group = gid;

               SimpleDataSet<ChatData> ds = new SimpleDataSet<ChatData>(ChatData.OBJECT_NAME);
               ds.Add(c);

               List<IDataSet> wrSet = new List<IDataSet>();
               wrSet.Add(ds);

               StringBuilder sb = new StringBuilder(wb.DocumentText);
               sb.Append("<br>");
               sb.Append(c.ToString());
               WriteChatText(sb.ToString());
               tbText.Clear();

               if (dsChatUser.ContainsKey(c.userid))
               {
                  ChatUser u = dsChatUser[c.userid];
                  string outsnd = u.outsnd;

                  if(outsnd.Trim().Length > 0)
                  {
                     Type t = typeof(System.Media.SystemSounds);
                     PropertyInfo i = t.GetProperty(outsnd, BindingFlags.Public | BindingFlags.Static);

                     if (i != null)
                     {
                        System.Media.SystemSound snd = i.GetValue(null, null) as System.Media.SystemSound;

                        if (snd != null)
                           snd.Play();
                     }
                  }
               }

               if (!DataModule.UpdateDataSet(wrSet, null, null, Config.GetConfig().GetConnection()))
                  DialogUtil.UpdateErrMsg(this);
            }
         }
      }

      private void tbText_KeyDown(object sender, KeyEventArgs e)
      {
         if (e.Control && e.KeyCode == Keys.Enter)
            btnSend.PerformClick();
      }

      private void btnNewGroup_Click(object sender, EventArgs e)
      {
         FmChatGroupEdit dlg = new FmChatGroupEdit();

         if (dlg.ShowDialog() == DialogResult.OK)
         {
            ChatGroup cg = new ChatGroup();
            cg.id = GRSoft.Network.DataObject.GenId();
            cg.title = dlg.Title;
            cg.owner = CurrentUser.user.User.id;
            CollectChatItems(cg.items, dlg.Items);      

            SimpleDataSet<ChatGroup> ds = new SimpleDataSet<ChatGroup>(ChatGroup.OBJECT_NAME);
            ds.Add(cg);

            List<IDataSet> wrSet = new List<IDataSet>();
            wrSet.Add(ds);

            if (!DataModule.UpdateDataSet(wrSet, null, null, Config.GetConfig().GetConnection()))
               DialogUtil.UpdateErrMsg(this);
            else
               lbGroup.Items.Add(cg);
         }
      }

      private void CollectChatItems(List<ChatGroup.ChatGroupItem> items, List<Agent> agents)
      {
         foreach (Agent a in agents)
         {
            ChatGroup.ChatGroupItem i = new ChatGroup.ChatGroupItem();
            i.id = a.id;
            i.agent = a;
            items.Add(i);
         }
      }

      private void FmChat_Load(object sender, EventArgs e)
      {
         refreshTimer = new System.Threading.Timer(refreshTimer_Tick, null, 0, 5000);
      }

      private void lbGroup_SelectedIndexChanged(object sender, EventArgs e)
      {
         LoadChat();
      }

      private void btnEditGroup_Click(object sender, EventArgs e)
      {
         ChatGroup cg = lbGroup.SelectedItem as ChatGroup;

         if (cg != null)
         {
            FmChatGroupEdit dlg = new FmChatGroupEdit();
            dlg.Title = cg.title;
            dlg.Items = CollectAgents(cg.items);

            if (dlg.ShowDialog() == DialogResult.OK)
            {
               cg.title = dlg.Title;
               cg.owner = CurrentUser.user.User.id;
               CollectChatItems(cg.items, dlg.Items);

               SimpleDataSet<ChatGroup> ds = new SimpleDataSet<ChatGroup>(ChatGroup.OBJECT_NAME);
               ds.Add(cg);

               List<IDataSet> wrSet = new List<IDataSet>();
               wrSet.Add(ds);

               if (!DataModule.UpdateDataSet(wrSet, null, null, Config.GetConfig().GetConnection()))
                  DialogUtil.UpdateErrMsg(this);
               else
                  lbGroup.Refresh();
            }
         }
      }

      private List<Agent> CollectAgents(List<ChatGroup.ChatGroupItem> list)
      {
         List<Agent> result = new List<Agent>();

         foreach (ChatGroup.ChatGroupItem i in list)
            if (i.agent != null)
               result.Add(i.agent);

         return result;
      }

      private void btnDelGroup_Click(object sender, EventArgs e)
      {
         ChatGroup cg = lbGroup.SelectedItem as ChatGroup;

         if (cg != null && DialogUtil.AskToDel(this))
         {
            cg.rem = 1;

            SimpleDataSet<ChatGroup> ds = new SimpleDataSet<ChatGroup>(ChatGroup.OBJECT_NAME);
            ds.Add(cg);

            List<IDataSet> wrSet = new List<IDataSet>();
            wrSet.Add(ds);

            if (!DataModule.UpdateDataSet(wrSet, null, null, Config.GetConfig().GetConnection()))
               DialogUtil.UpdateErrMsg(this);
            else
               lbGroup.Items.Remove(cg);
         }
      }

      private void btnSetting_Click(object sender, EventArgs e)
      {
         new FmChatSetting().Show();
      }
   }
}
