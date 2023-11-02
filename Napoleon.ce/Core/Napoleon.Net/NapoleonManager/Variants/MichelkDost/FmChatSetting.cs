using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmChatSetting : Form
   {
      DataSet<string, ChatUser> dsChatUser;
      List<string> sounds = new List<string>();

      public FmChatSetting()
      {
         InitializeComponent();

         dsChatUser = (DataSet<string, ChatUser>)DataModule.Get(ChatUser.OBJECT_NAME) ?? new DataSet<string, ChatUser>(ChatUser.OBJECT_NAME);
      }

      private void btnOK_Click(object sender, EventArgs e)
      {
         List<IDataSet> list = new List<IDataSet>();
         DataSet<string, ChatUser> ds = new DataSet<string, ChatUser>(ChatUser.OBJECT_NAME, false);
         ChatUser u = new ChatUser();
         u.id = CurrentUser.user.User.id;
         u.name = tbName.Text.Trim();
         u.incsnd = cbIncSnd.SelectedItem.ToString();
         u.outsnd = cbOutSnd.SelectedItem.ToString();

         ds.Add(u.id, u);
         list.Add(ds);

         if (!DataModule.WriteDataSet(list, Config.GetConfig().GetConnection()))
            DialogUtil.UpdateErrMsg(this);
         else
            Close();
      }

      private void FmChatSetting_Load(object sender, EventArgs e)
      {
         string id = CurrentUser.user.User.id;

         if(dsChatUser.ContainsKey(id))
         {
            sounds.Add("");
            sounds.Add("Asterisk");
            sounds.Add("Beep");
            sounds.Add("Exclamation");
            sounds.Add("Hand");
            sounds.Add("Question");

            cbIncSnd.Items.AddRange(sounds.ToArray());
            cbOutSnd.Items.AddRange(sounds.ToArray());
            cbIncSnd.SelectedIndex = 0;
            cbOutSnd.SelectedIndex = 0;

            ChatUser u = dsChatUser[id];
            tbName.Text = u.name;
            cbIncSnd.SelectedItem = u.incsnd;
            cbOutSnd.SelectedItem = u.outsnd;
         }
      }
   }
}
