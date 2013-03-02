/*
 * 
 * pthread debugging helper
 *
 * define PTHREAD_DEBUG to enable
 *
 * use the macros listed at the end of the header
 * rather than the usual pthread_* calls
 *
 * (C) Chris Riley, 2003.
 * 
 */


#ifndef PTHREAD_DEBUG_H_
#define PTHREAD_DEBUG_H_

#ifdef HAVE_ERRNO_H
	#include <errno.h>
#endif

#ifdef PTHREAD_DEBUG

int pec_;
int pec__;

#define ERROR_CODE(x) \
	switch (x) \
	{ \
		case EDEADLK: \
			printf("EDEADLK"); \
			break; \
\
		case EINVAL: \
			printf("EINVAL"); \
			break; \
\
		case EBUSY: \
			printf("EBUSY"); \
			break; \
\
		case EPERM: \
			printf("EPERM"); \
			break; \
\
		case ENOSYS: \
			printf("ENOSYS"); \
			break; \
\
		default: \
			printf("unknown (code %d)",pec__); \
	}

#define MUTEX_LOCK(mutex) \
	if ((pec_=pthread_mutex_lock(mutex)) != 0) \
	{ \
		pec__=pec_; \
		printf("\n** ERROR: %s line %d: pthread_mutex_lock() failed, code: ",__FILE__,__LINE__); \
		ERROR_CODE(pec__); \
		printf("\n\n"); \
		exit(1); \
	}

#define COND_WAIT(cv,mutex) \
	if (pthread_cond_wait(cv,mutex) != 0) \
	{ \
		pec__=pec_; \
		printf("\n** ERROR: %s line %d: pthread_cond_wait() failed, code: ",__FILE__,__LINE__); \
		ERROR_CODE(pec__); \
		printf("\n\n"); \
		exit(1); \
	}

#define MUTEX_UNLOCK(mutex) \
	if ((pec_=pthread_mutex_unlock(mutex)) != 0) \
	{ \
		pec__=pec_; \
		printf("\n** ERROR: %s line %d: pthread_mutex_unlock() failed, code: ",__FILE__,__LINE__); \
		ERROR_CODE(pec__); \
		printf("\n\n"); \
		exit(1); \
	}

#define COND_SIGNAL(cv) \
	if (pthread_cond_signal(cv) != 0) \
	{ \
		pec__=pec_; \
		printf("\n** ERROR: %s line %d: pthread_cond_signal() failed, code: ",__FILE__,__LINE__); \
		ERROR_CODE(pec__); \
		printf("\n\n"); \
		exit(1); \
	}

#else

#define MUTEX_LOCK(mutex) pthread_mutex_lock(mutex);
#define COND_WAIT(cv,mutex) pthread_cond_wait(cv,mutex);
#define MUTEX_UNLOCK(mutex) pthread_mutex_unlock(mutex);
#define COND_SIGNAL(cv) pthread_cond_signal(cv);

#endif

#endif
