import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AppComponent } from './app.component';

import { SignupComponent } from './signup/signup.component';
import { HomeComponent } from './home/home.component';
import { NgxUiLoaderModule, NgxUiLoaderConfig, SPINNER } from 'ngx-ui-loader';
import { MatDialogModule } from '@angular/material/dialog'; 
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon'; 
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button'; 
import { MatGridListModule } from '@angular/material/grid-list'; 
import { MatInputModule } from '@angular/material/input';

import { LoginComponent } from './login/login.component'; 

const ngxUiLoaderConfig: NgxUiLoaderConfig = {
  text: "Loading",
  textColor: "#FFFFFF",
  textPosition: "center-center",
  bgsColor: "#7b1fa2",
  fgsColor: "#7b1fa2",
  fgsType: SPINNER.rectangleBounce,
  fgsSize: 100,
  hasProgressBar: false
};

@NgModule({
  declarations: [
    AppComponent,
    SignupComponent,
    HomeComponent,
    LoginComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    MatDialogModule,
    BrowserAnimationsModule,
    NgxUiLoaderModule.forRoot(ngxUiLoaderConfig),
    MatToolbarModule,
    MatIconModule,
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatGridListModule,
    MatButtonModule,
    MatInputModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
