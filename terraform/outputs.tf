output "vpc_id" {
  value = aws_vpc.main.id
}

output "public_subnet_id" {
  value = aws_subnet.aws_subnet_public_a.id
}

output "load_balancer_dns_name" {
  description = "DNS name of the Load Balancer"
  value       = aws_lb.loadbalancer.arn
}

output "public_ip" {
  description = "Public IP Address for the Public IP"
  value       = "http://${aws_instance.web_server.public_ip}/"
}