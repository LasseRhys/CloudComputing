terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = ">= 4.66.1"
    }
  }

  required_version = ">= 1.5.5"
}


provider "aws" {
  region = "us-east-1"
  access_key = var.aws_access_key
  secret_key = var.aws_secret_key
  token = var.aws_token
  # Authentication requires the following environment variables:
  #     AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY and AWS_SECRET_ACCESS_KEY
}