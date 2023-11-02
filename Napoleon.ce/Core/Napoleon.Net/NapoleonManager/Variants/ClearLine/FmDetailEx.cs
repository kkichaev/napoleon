using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
    [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
    [System.Runtime.InteropServices.ComVisibleAttribute(true)]
    public class FmDetailEx : FmDetail
    {
        private DataSet<int, ArchSales> dsArchSales;
        private DataSet<int, PicStore> dsPicStore;
        private Dictionary<string, PicStore> picMap = new Dictionary<string, PicStore>();

        public FmDetailEx(FmDetailData data)
           : base(data)
        {
            dsArchSales = (DataSet<int, ArchSales>)DataModule.Get(ArchSales.OBJECT_NAME) ??
               new DataSet<int, ArchSales>(ArchSales.OBJECT_NAME);
            dsPicStore = new DataSet<int, PicStore>(PicStore.OBJECT_COPY_NAME);
            documents.Add(new DocumentInfo(dsArchSales, ObjType.TObjType.ArchSales));
        }

        protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
        {
            base.BeforeRefreshData(updSets, agentID, dateBegin, dateEnd);
            string filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
            dsArchSales.Filter = filter;
            dsPicStore.Filter = filter;

            updSets.Add(dsArchSales);
            updSets.Add(dsPicStore);
        }

        internal override System.Windows.Forms.Control RefreshDetail(OrderDetailRepresentation odr)
        {
            Control result = null;
            if (odr.Doctype.Val == ObjType.TObjType.ArchSales)
            {
                result = dgvOrderItems;
                SetOrderItems(odr.StoreObject as Order);
            }

            return result;
        }

        protected override void AfterRefreshData()
        {
            base.AfterRefreshData();
            picMap.Clear();

            foreach (PicStore p in dsPicStore.Data)
            {
                picMap[p.id] = p;
            }
        }

        void AddAnswerPhotos(StringBuilder htmlBuilder, Answer a)
        {
            foreach (AnswerItem ai in a.items)
            {
                if (ai.type == QuestionItem.IMAGE && picMap.ContainsKey(ai.answer))
                {
                    PicStore ps = picMap[ai.answer];
                    AddPhotoToHtml(htmlBuilder, ai.id, ps.name, ps.smallName, ps.smallSize, a.created.ToString("dd.MM.yy HH:mm"), ps.created);
                }
            }
        }

        protected override void AddObjectPhoto(StringBuilder htmlBuilder, Network.DataObject dataObject)
        {
            base.AddObjectPhoto(htmlBuilder, dataObject);

            ScriptDoc sd = dataObject as ScriptDoc;
            if(sd != null)
            {
                foreach(ScriptDocItem sdi in sd.items)
                {
                    if(sdi.type == Answer.OBJECT_NAME)
                    {
                        Answer a = sdi.Document as Answer;
                        if(a != null)
                            AddAnswerPhotos(htmlBuilder, a);
                    }
                }
            } else
            {
                Answer a = dataObject as Answer;
                if (a != null)
                {
                    AddAnswerPhotos(htmlBuilder, a);
                }

            }
        }
    }
}
