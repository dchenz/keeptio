package com.keeptio.controllers;

public interface WizardController extends Controller {
	public boolean tryPrevious();
	public boolean tryNext();
	public String getTitle();
}
