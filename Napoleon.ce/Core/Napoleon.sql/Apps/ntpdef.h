/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Network Time Protocol Definition
 *
 *  ert   18/12/2007   creating
 */


#ifndef _NTP_DEF_H
#define _NTP_DEF_H

typedef unsigned char u_char;
typedef signed char s_char;
typedef unsigned long u_fp;
typedef unsigned long u_int32;
typedef long int32;

typedef struct {
	union {
		u_int32 Xl_ui;
		int32 Xl_i;
	} Ul_i;
	union {
		u_int32 Xl_uf;
		int32 Xl_f;
	} Ul_f;
} l_fp;
 
#define MAX_MAC_LEN	5 * sizeof(u_int32)	/* MD5 */

struct pkt {
	u_char	li_vn_mode;	/* leap indicator, version and mode */
	u_char	stratum;	/* peer stratum */
	u_char	ppoll;		/* peer poll interval */
	s_char	precision;	/* peer clock precision */
	u_fp	rootdelay;	/* distance to primary clock */
	u_fp	rootdispersion;	/* clock dispersion */
	u_int32	refid;		/* reference clock ID */
	l_fp	reftime;	/* time peer clock was last updated */
	l_fp	org;		/* originate time stamp */
	l_fp	rcv;		/* receive time stamp */
	l_fp	xmt;		/* transmit time stamp */
};

/*
 * Stuff for extracting things from li_vn_mode
 */
#define	PKT_MODE(li_vn_mode)	((u_char)((li_vn_mode) & 0x7))
#define	PKT_VERSION(li_vn_mode)	((u_char)(((li_vn_mode) >> 3) & 0x7))
#define	PKT_LEAP(li_vn_mode)	((u_char)(((li_vn_mode) >> 6) & 0x3))

/*
 * Stuff for putting things back into li_vn_mode
 */
#define	PKT_LI_VN_MODE(li, vn, md) \
	((u_char)((((li) << 6) & 0xc0) | (((vn) << 3) & 0x38) | ((md) & 0x7)))

#define TIME_SERVICE_PORT   123


#endif
