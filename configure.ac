dnl Process this file with autoconf
AC_INIT([muksvc], [1.2.5])
AC_PREREQ(2.69)
AM_INIT_AUTOMAKE([foreign nostdinc])
dnl program checks
AC_PROG_LN_S
AC_PROG_SED

AC_ARG_ENABLE([debug], [ --enable-debug>Development level debug],
[case "${enableval}" in
	yes) debug=true ;;
	no) debug=false ;;
	*) AC_MSG_ERROR([bad value ${enableval} for --enable-debug]) ;;
esac],[debug=false])

AM_CONDITIONAL([DEBUG], [test x$debug = xtrue])

AC_OUTPUT([Makefile])

