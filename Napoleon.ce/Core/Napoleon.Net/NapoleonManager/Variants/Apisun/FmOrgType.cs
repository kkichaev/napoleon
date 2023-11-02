using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;

namespace GRSoft.NapoleonManager
{
   public partial class FmOrgType : Form
   {
      DataSet<string, OrgType> dsOrgType;
      DataSet<string, OrgType> dsDelOrgType;
      private SearchEngine searchEngine;

      public FmOrgType()
      {
         InitializeComponent();

         dsOrgType = (DataSet<string, OrgType>) DataModule.Get(OrgType.OBJECT_NAME) ??
            new DataSet<string, OrgType>(OrgType.OBJECT_NAME);
         dsDelOrgType = new DataSet<string, OrgType>(OrgType.OBJECT_NAME);
         btnSave.Enabled = false;
         btnAdd.Enabled = false;
         btnEdit.Enabled = false;
         btnDel.Enabled = false;

         searchEngine = new SearchEngine(new FindDataGridObject(dgvOrgType, 1)); 
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         DataModule.SetDataRepsonceHandlers(DataProcessed, DataConnectionError);

         List<IDataSet> updSet = new List<IDataSet>();
         updSet.Add(dsOrgType);

         FmWait.ShowForm(this, DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(),
            updSet, FmWait.ProgressIndicator));

         btnAdd.Enabled = true;
         btnEdit.Enabled = true;
         btnDel.Enabled = true;
      }

      //Окончание выборки, заполняются внутренние наборы
      void DataProcessed(System.Object setnder, EventArgs e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();

         Invoke(new EmptyParamHandler(delegate()
         {
            List<OrgType> list = new List<OrgType>();
            list.AddRange(dsOrgType.Values);
            list.Sort(new Comparison<OrgType>(delegate(OrgType ot1, OrgType ot2) { return ot1.id.CompareTo(ot2.id); }));

            BindingList<OrgType> blist = new BindingList<OrgType>(list);
            dgvOrgType.DataSource = blist;
         }));
      }

      //Произошла ошибка в соединении
      private void DataConnectionError(EDataResponse e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();

         Invoke(new EmptyParamHandler(delegate
         {
            const string TITLE = "Ошибка";

            MessageBox.Show(e.Msg, TITLE, MessageBoxButtons.OK,
               MessageBoxIcon.Error);
         }));
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         OrgType ot = FmOrgTypeEdit.Edit(null);

         if (ot != null)
         {
            if (!dsOrgType.ContainsKey(ot.id))
               dsOrgType.Add(ot.id, ot);
            else
               dsOrgType[ot.id] = ot;

            BindingList<OrgType> list = (BindingList<OrgType>)dgvOrgType.DataSource;
            
            if(list == null)
            {
               list = new BindingList<OrgType>();
               dgvOrgType.DataSource = list;
            }

            list.Add(ot);

            btnSave.Enabled = true;
         }
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         List<IDataSet> wrSet = new List<IDataSet>();
         List<IDataSet> rmvSet = new List<IDataSet>();

         if (dsOrgType.Count > 0)
            wrSet.Add(dsOrgType);

         if (dsDelOrgType.Count > 0)
            rmvSet.Add(dsDelOrgType);
         
         if (!DataModule.UpdateDataSet
            (wrSet, rmvSet, null, Config.GetConfig().GetConnection()))
            MessageBox.Show("Ошибка записи в базу данных", "Ошибка", MessageBoxButtons.OK,
               MessageBoxIcon.Error);
         else
            btnSave.Enabled = false;
      }

      private void btnDel_Click(object sender, EventArgs e)
      {

         DataGridViewRow row = dgvOrgType.CurrentRow;

         if (row != null && MessageBox.Show("Запись будет удалена, удалить?",
            "Вопрос", MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK)
         {
            OrgType ot = (OrgType)row.DataBoundItem;
            dsDelOrgType.Add(ot.id, ot);
            dgvOrgType.Rows.Remove(row);
            btnSave.Enabled = true;
         }
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         DataGridViewRow row = dgvOrgType.CurrentRow;

         if (row != null)
         {
            OrgType ot = (OrgType)row.DataBoundItem;

            OrgType orgType = FmOrgTypeEdit.Edit(ot);

            if (orgType != null)
            {
               btnSave.Enabled = true;
            }
         }
      }

      private void FmOrgType_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (btnSave.Enabled == true && MessageBox.Show("Сохранить изменения?", "Вопрос",
            MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK)
         {
            btnSave_Click(btnSave, EventArgs.Empty);
         }
      }

      private void btnFindUp_Click(object sender, EventArgs e)
      {
         searchEngine.find(tbFind.Text, Direction.UP);
      }

      private void btnFindDown_Click(object sender, EventArgs e)
      {
         searchEngine.find(tbFind.Text, Direction.DOWN);
      }

      private void tbFind_KeyDown(object sender, KeyEventArgs e)
      {
         if (e.KeyCode == Keys.Enter)
            searchEngine.find(tbFind.Text, Direction.DOWN);
      }
   }

   public class OrgType : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "OrgType";

      [KeyField]
      public string id = string.Empty;
      public string name = string.Empty;

      public string Id { get { return id; } }
      public string Name { get { return name; } }

      public override string ToString()
      {
         return Name;
      }
   }
}
