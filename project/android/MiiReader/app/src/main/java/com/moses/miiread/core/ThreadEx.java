package com.moses.miiread.core;

public class ThreadEx extends Thread
{
	private Runnable m_runnable;
	private boolean m_bIsTryStop = false;
	private boolean m_bIsTryPause = false;

	//
	public ThreadEx(String strThreadName, Runnable runnable)
	{
		super(strThreadName);
		m_runnable = runnable;
	}

	public ThreadEx(String strThreadName)
	{
		super(strThreadName);
		m_runnable = null;
	}

	public ThreadEx(Runnable runnable)
	{
		m_runnable = null;
	}

	@Override
	public void run()
	{
		if(m_runnable != null)
			m_runnable.run();
	}

	public static boolean trySleep(long lTime)
	{
		try
		{
			Thread.sleep(lTime);
			return true;
		}
		catch(InterruptedException e)
		{
			Thread.currentThread().interrupt();
		}
		return false;
	}

	public boolean tryJoin()
	{
		try
		{
			join();
			return true;
		}
		catch(InterruptedException e)
		{
			Thread.currentThread().interrupt();
		}
		return false;
	}

	public void tryStop()
	{
		this.m_bIsTryStop = true;
	}

	public boolean isTryStop()
	{
		return m_bIsTryStop;
	}

	public void tryPause()
	{
		this.m_bIsTryPause = true;
	}

	public boolean isTryPause()
	{
		return m_bIsTryPause;
	}

	public void tryResume()
	{
		this.m_bIsTryPause = false;
	}

	public boolean isTryResume()
	{
		return !m_bIsTryPause;
	}
}
