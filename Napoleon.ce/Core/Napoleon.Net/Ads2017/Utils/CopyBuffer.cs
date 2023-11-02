using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Ads2017
{
    public class CopyBuffer
    {
        private static CopyBuffer instance = new CopyBuffer();

        public static CopyBuffer Instance()
        {
            return instance;
        }

        private CopyBuffer()
        {
        }

        public enum CommandType { Cut, Copy }

        private object stored = null;
        private CommandType command = CommandType.Copy;

        public object Stored
        {
            get { return stored; }
            set { stored = value; }
        }

        public CommandType Command
        {
            get { return command; }
            set { command = value; }
        }

        public bool IsEmpty
        {
            get { return stored == null; }
        }

        internal void Clear()
        {
            stored = null;
        }
    }
}
