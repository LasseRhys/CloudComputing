variable "aws_region" {
  default = "eu-central-1"
}

variable "vpc_cidr" {
  default = "10.0.0.0/16"
}
variable "aws_access_key" {
  description = "AWS access key"
  type        = string
}
variable "aws_secret_key"{
  description = "AWS secret key"
  type        = string
}
variable "aws_token"  {
  description = "AWS token"
  type        = string
}
variable "username"  {
  description = "Database Username"
  default     = "postgres"
  type        = string
}
variable "password"  {
  description = "Database Password"
  type        = string
}

